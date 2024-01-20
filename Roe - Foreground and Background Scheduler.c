/* This is the only file you will be editing.
 * - Copyright of Starter Code: Prof. Kevin Andrea, George Mason University.  All Rights Reserved
 * - Copyright of Student Code: Bryce Roe 
 * - Restrictions on Student Code: Do not post your code on any public site (eg. Github).
 * -- Feel free to post your code on a PRIVATE Github and give interviewers access to it.
 * -- You are liable for the protection of your code from others.
 * - Date: Jan 2023
 */

/* Fill in your Name and GNumber in the following two comment fields
 * Name: Bryce Roe
 * GNumber: 01310206
 */

// System Includes
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/time.h>
#include <pthread.h>
#include <sched.h>
// Local Includes
#include "op_sched.h"
#include "vm_support.h"
#include "vm_process.h"

// Flags
#define CRITICAL_FLAG   (1 << 31) 
#define LOW_FLAG        (1 << 30) 
#define READY_FLAG      (1 << 29)
#define DEFUNCT_FLAG    (1 << 28)

// This is the global variable for the max age.
#define MAX_AGE 5

/* Feel free to create any helper functions you like! */

/* Initializes the Op_schedule_s Struct and all of the Op_queue_s Structs
 * Follow the project documentation for this function.
 * Returns a pointer to the new Op_schedule_s or NULL on any error.
 */
Op_schedule_s *op_create() {
  // The schedule itself is given memory.
  Op_schedule_s *new_sched = malloc(sizeof(Op_schedule_s));
  if (new_sched == NULL) {
    return NULL;
  }

  // The high queue is given memory, the head is made null, and 
  // the count is made 0.
  new_sched->ready_queue_high = malloc(sizeof(Op_queue_s));
  if (new_sched->ready_queue_high == NULL) {
    free(new_sched);
    return NULL;
  }
  new_sched->ready_queue_high->head = NULL;
  new_sched->ready_queue_high->count = 0;

  // The low queue is given memory, the head is made null, and 
  // the count is made 0.
  new_sched->ready_queue_low = malloc(sizeof(Op_queue_s));
  if (new_sched->ready_queue_low == NULL) {
    free(new_sched);
    return NULL;
  }
  new_sched->ready_queue_low->head = NULL;
  new_sched->ready_queue_low->count = 0;

  // The defunct queue is given memory, the head is made null, and 
  // the count is made 0.
  new_sched->defunct_queue = malloc(sizeof(Op_queue_s));
  if (new_sched->defunct_queue == NULL) {
    free(new_sched);
    return NULL;
  }
  new_sched->defunct_queue->head = NULL;
  new_sched->defunct_queue->count = 0;

  // The schedule is returned.
  return new_sched; 
}

/* Create a new Op_process_s with the given information.
 * - Malloc and copy the command string, don't just assign it!
 * Follow the project documentation for this function.
 * Returns the Op_process_s on success or a NULL on any error.
 */
Op_process_s *op_new_process(char *command, pid_t pid, int is_low, int is_critical) {
  // The new process node is made and the bit-mask is started.
  Op_process_s *new_process;
  int mask = 0x00000;
  
  // A process can't be low and critical, so it is made null.
  if (is_low != 0 && is_critical != 0) {
    return NULL;
  }

  // Space is allocated to the process.
  new_process = malloc(sizeof(Op_process_s));
  if (new_process == NULL) {
    return NULL;
  }

  // This copies over the command with the proper memory space.
  new_process->cmd = malloc(strlen(command) + 1);
  if (command == NULL) {
    free(new_process);
    return NULL;
  }
  strncpy(new_process->cmd, command, strlen(command) + 1);
  
  // And this imports the pid.
  new_process->pid = pid;

  // This sets the Ready State to 1.
  mask = mask | READY_FLAG;
  
  // Defunct is already 0.

  // The low and critical flags are set when needed.
  if (is_low != 0) {
    mask = mask | LOW_FLAG;
  }

  if (is_critical != 0) {
    mask = mask | CRITICAL_FLAG;
  }

  // Fill in all flags, make age 0, and the next null.

  new_process->state = mask;
  new_process->age = 0;
  new_process->next = NULL;

  // The process is returned.
  return new_process; 
}

/* Adds a process into the appropriate singly linked list queue.
 * Follow the project documentation for this function.
 * Returns a 0 on success or a -1 on any error.
 */
int op_add(Op_schedule_s *schedule, Op_process_s *process) {
  // Current tracks the position of the queue.
  Op_process_s *current;

  // Returns -1 if schedule is null.
  if (schedule != NULL) {
    // Returns -1 if process is null.
    if (process != NULL) {
      // The defunct flag is turned off and the ready flag is turned on.
      process->state = process->state & ~(DEFUNCT_FLAG);
      process->state = process->state | READY_FLAG;

      // If the low flag is checked:
      if (!!(process->state & LOW_FLAG)) {
        // When it is low:
        // The process is added to the end of the list, made the head if 
        // needed, and the count is upped. 0 is returned.
        if (schedule->ready_queue_low->head == NULL) {
          schedule->ready_queue_low->head = process;
        }
        else {
          current = schedule->ready_queue_low->head;
          while (current->next != NULL) {
            current = current->next;
          }
          current->next = process;
        }
        schedule->ready_queue_low->count++;
        return 0;
      }
      else {
        // When it is high:
        // The process is added to the end of the list, made the head if 
        // needed, and the count is upped. 0 is returned.
        if (schedule->ready_queue_high->head == NULL) {
          schedule->ready_queue_high->head = process;
        }
        else {
          current = schedule->ready_queue_high->head;
          while (current->next != NULL) {
            current = current->next;
          }
          current->next = process;
        }
        schedule->ready_queue_high->count++;
        return 0;
      }
    }
  }
  // -1 returns from errors.
  return -1;
}

/* Returns the number of items in a given Op_queue_s
 * Follow the project documentation for this function.
 * Returns the number of processes in the list or -1 on any errors.
 */
int op_get_count(Op_queue_s *queue) {
  // The queue count is returned if the queue isn't null.
  if (queue == NULL) {
    return -1;
  }
  return queue->count;
}

/* Selects the next process to run from the High Ready Queue.
 * Follow the project documentation for this function.
 * Returns the process selected or NULL if none available or on any errors.
 */
Op_process_s *op_select_high(Op_schedule_s *schedule) {
  // The storage for the highest node, the current iteration, and the previous node
  // are made.
  Op_process_s *highest;
  Op_process_s *current;
  Op_process_s *previous = NULL;

  // The null comes back if the schedule is null.
  if (schedule == NULL) {
    return NULL;
  }

  // The null comes back if the head is null.
  highest = schedule->ready_queue_high->head;
  if (highest == NULL) {
    return NULL;
  }
  
  // The nodes are iterated through.
  current = schedule->ready_queue_high->head;
  while (current != NULL) {
    // Critical flags are prioritized in the queue.
    if (!!(current->state & CRITICAL_FLAG)) {
      highest = current;

      // The head is removed and returned right away if it is critical.
      if (current == schedule->ready_queue_high->head) {
        schedule->ready_queue_high->head = schedule->ready_queue_high->head->next;
        schedule->ready_queue_high->count--;
        highest->age = 0;
        highest->next = NULL;
        return highest;
      }
      // The node is removed and returned right away if it is critical.
      if (previous != NULL) {
        previous->next = current->next;
      }
      schedule->ready_queue_high->count--;
      highest->age = 0;
      highest->next = NULL;
      return highest;
    }
    // Iteration occurs here.
    previous = current;
    current = current->next;
  }

  // The head is removed and comes back if there are no critical values.
  schedule->ready_queue_high->head = schedule->ready_queue_high->head->next;
  schedule->ready_queue_high->count--;
  highest->age = 0;
  highest->next = NULL;
  return highest;
}

/* Schedule the next process to run from the Low Ready Queue.
 * Follow the project documentation for this function.
 * Returns the process selected or NULL if none available or on any errors.
 */
Op_process_s *op_select_low(Op_schedule_s *schedule) {
  // The highest process node is initialized.
  Op_process_s *highest;

  // The null comes back if the schedule is null.
  if (schedule == NULL) {
    return NULL;
  }

  // The null comes back if the head is null.
  highest = schedule->ready_queue_low->head;
  if (highest == NULL) {
    return NULL;
  }

  // The head is removed and comes back.
  schedule->ready_queue_low->head = schedule->ready_queue_low->head->next;
  schedule->ready_queue_low->count--;
  highest->age = 0;
  highest->next = NULL;
  return highest;
}

/* Add age to all processes in the Ready - Low Priority Queue, then
 *  promote all processes that are >= MAX_AGE.
 * Follow the project documentation for this function.
 * Returns a 0 on success or -1 on any errors.
 */
int op_promote_processes(Op_schedule_s *schedule) {
  // The storage for the current iteration, and the previous node, the next one,
  // and the position node are made.
  Op_process_s *current;
  Op_process_s *temp_next;
  Op_process_s *position;
  Op_process_s *previous = NULL;
  // This checker is for making sure no nodes get skipped over upon node removal.
  int checker = 0;

  // The null comes back if the schedule is null.
  if (schedule == NULL) {
    return -1;
  }

  // This iterates through the low queue.
  current = schedule->ready_queue_low->head;
  while (current != NULL) {
    checker = 0;
    // Age goes up by one.
    current->age++;
    if (current->age >= MAX_AGE) {
      // If this node is the head, the next node is set as the node as well and
      // the first node is promoted up.
      if (previous == NULL) {
        schedule->ready_queue_low->head = current->next;
        temp_next = current->next;
        current->next = NULL;
        current->age = 0;
        schedule->ready_queue_low->count--;

        // Add to high time with head and body variation.
        if (schedule->ready_queue_high->head == NULL) {
          schedule->ready_queue_high->head = current;
        }
        else {
          // This iterates to the end of the high queue.
          position = schedule->ready_queue_high->head;
          while (position->next != NULL) {
            position = position->next;
          }
          position->next = current;
        }
        current = temp_next;
        schedule->ready_queue_high->count++;
      }
      else {
        // Add to high time with this one, since it's not the head.
        previous->next = current->next;

        // Same stuff here from before.
        if (schedule->ready_queue_high->head == NULL) {
          schedule->ready_queue_high->head = current;
        }
        else {
          position = schedule->ready_queue_high->head;
          while (position->next != NULL) {
            position = position->next;
          }
          position->next = current;
        }
        schedule->ready_queue_high->count++;
      }
      checker = 1;
      
    }

    // Iteration continues like normal if it hasn't iterated already.
    if (checker == 0) {
      previous = current;
      current = current->next; 
    }
  }
  // 0 goes back when it is done.
  return 0;
}

/* This is called when a process exits normally.
 * Put the given node into the Defunct Queue and set the Exit Code 
 * Follow the project documentation for this function.
 * Returns a 0 on success or a -1 on any error.
 */
int op_exited(Op_schedule_s *schedule, Op_process_s *process, int exit_code) {
  // The iteration and previous nodes are initialized.
  Op_process_s *iteration;
  Op_process_s *previous = NULL;

  // -1 is returned if either schedule or process is null.
  if (schedule != NULL) {
    if (process != NULL) {

      // The defunct flag and exit code goes on, and the ready flag comes off.
      process->state = process->state | DEFUNCT_FLAG;
      process->state = process->state & ~(READY_FLAG);
      process->state = process->state & 0xF0000;
      process->state = process->state | exit_code;

      // The defunct queue is iterated through until the end and the process is 
      // added to the end.

      // It needs a little extra for the head position. 0 goes back upon success.
      iteration = schedule->defunct_queue->head;
      if (iteration == NULL) {
        schedule->defunct_queue->head = process;
        schedule->defunct_queue->count++;
        return 0;
      }

      while (iteration != NULL) {
        previous = iteration;
        iteration = iteration->next;
      }
      
      previous->next = process;
      schedule->defunct_queue->count++;
      return 0;
    }
  }
  
  return -1;
}

/* This is called when the OS terminates a process early.
 * Remove the process with matching pid from Ready High or Ready Low and add the Exit Code to it.
 * Follow the project documentation for this function.
 * Returns a 0 on success or a -1 on any error.
 */
int op_terminated(Op_schedule_s *schedule, pid_t pid, int exit_code) {
  // The current, iteration, and previous nodes are initialized.
  Op_process_s *current;
  Op_process_s *iteration;
  Op_process_s *previous = NULL;

  // -1 goes back if the schedule is null.
  if (schedule != NULL) {
    // First for high queue is searched:

    // It is iterated through.
    current = schedule->ready_queue_high->head;
    while (current != NULL) {
      // If the pid matches:
      if (current->pid == pid) {
        // If it is the head or not, the processes get adjusted accordingly.
        if (previous == NULL && current == schedule->ready_queue_high->head) {
          schedule->ready_queue_high->head = schedule->ready_queue_high->head->next;
        }
        else {
          previous->next = current->next;
        }
        // Next is made null and count is lowered.
        current->next = NULL;
        schedule->ready_queue_high->count--;

        // The same state adjustments from the last method again.
        current->state = current->state | DEFUNCT_FLAG;
        current->state = current->state & ~(READY_FLAG);
        current->state = current->state & 0xF0000;
        current->state = current->state | exit_code;

        // The defunct process gets put at the end of the defunct queue and 0 is returned.
        iteration = schedule->defunct_queue->head;
        if (iteration == NULL) {
          schedule->defunct_queue->head = current;
          schedule->defunct_queue->count++;
          return 0;
        }
        while (iteration != NULL) {
          previous = iteration;
          iteration = iteration->next;
        }
        

        previous->next = current;
        schedule->defunct_queue->count++;
        return 0;
      }

      // Iteration occurs here.
      previous = current;
      current = current->next;
    }

    // Then the low queue is searched:

    current = schedule->ready_queue_low->head;
    previous = NULL;
    while (current != NULL) {
      // If the pid matches:
      if (current->pid == pid) {
        // If it is the head or not, the processes get adjusted accordingly.
        if (previous == NULL) {
          schedule->ready_queue_low->head = schedule->ready_queue_low->head->next;
        }
        else {
          previous->next = current->next;
        }
        // Next is made null and count is lowered.
        current->next = NULL;
        schedule->ready_queue_low->count--;

        // The same state adjustments from the last method again.
        current->state = current->state | DEFUNCT_FLAG;
        current->state = current->state & ~(READY_FLAG);
        current->state = current->state & 0xF0000;
        current->state = current->state | exit_code;

        // The defunct process gets put at the end of the defunct queue and 0 is returned.
        iteration = schedule->defunct_queue->head;
        if (iteration == NULL) {
          schedule->defunct_queue->head = current;
          schedule->defunct_queue->count++;
          return 0;
        }

        while (iteration != NULL) {
          previous = iteration;
          iteration = iteration->next;
        }
    
        previous->next = current;
        schedule->defunct_queue->count++;
        return 0;
      }

      //Iteration occurs here.
      previous = current;
      current = current->next;
    }
    
  }
  return -1;
}

/* Frees all allocated memory in the Op_schedule_s, all of the Queues, and all of their Nodes.
 * Follow the project documentation for this function.
 */
void op_deallocate(Op_schedule_s *schedule) {
  // Current and next are used to iterate through all the queues.
  Op_process_s *current = schedule->ready_queue_high->head;
  Op_process_s *next = NULL;
  // The high queue cmd and processes are freed.
  while (current != NULL) {
    next = current->next;
    free(current->cmd); 
    free(current);
    current = next;
  }

  // The low queue cmd and processes are freed.
  current = schedule->ready_queue_low->head;
  while (current != NULL) {
    next = current->next;
    free(current->cmd);
    free(current);
    current = next;
  }

  // The defunct queue cmd and processes are freed.
  current = schedule->defunct_queue->head;
  while (current != NULL) {
    next = current->next;
    free(current->cmd);
    free(current);
    current = next;
  }

  // All the queues are freed and then the schedule is freed.
  free(schedule->ready_queue_high);
  free(schedule->ready_queue_low);
  free(schedule->defunct_queue);
  free(schedule);
}
