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
 *
 * Note: This file is a part of a larger application, which this code calculates for.
 *       We were tasked with creating a calculator capable of adding, subtracting, and 
 *       multiplying together all values capable of being represented with 7 bits and 
 *       without denormalization. This file is meant to be run with the accompanying 
 *       tar'ed files. 
 *
 *       We were told we could use decimal numbers in our arithmetic, 
 *       however, I created the multiplication function using only hexadecimal.
 */

#include <stdio.h>
#include <stdlib.h>
#include "common_structs.h"
#include "common_definitions.h"
#include "common_functions.h"
#include "tinysf.h"

// Feel free to add many Helper Functions, Consts, and Definitions!

#define BIAS 7

// ----------Public API Functions (write these!)-------------------

tinysf_s negateTinySF();

/* shiftCounter - Counts the number of empty bits to the left of the given 
    - value.
 * Return the count of shifts to the end.
 */
int shiftCounter(unsigned int number) {
  int shift = 0;
  unsigned int value = number;
  while (!(value >= 0x80000000) && !(shift >= 32)) {
    value = value << 1;
    shift++;
  }
  return shift;
}

/* rightZeroCounter - Counts all empty bits from the right of the given value.
 * Return the count of zeros until a bit is found from the right.
 */
int rightZeroCounter(unsigned int number) {
  int shift = 0;
  unsigned int value = number;
  while ((value & 0x80000000) != 0x00000000) {
    value = value >> 1;
    shift++;
  }
  return shift;
}

/* leftZeroCounter - Counts all empty bits from the left of the given value.
 * Return the count of zeros until a bit is found from the left.
 */
int leftZeroCounter(unsigned int number) {
  int shift = 0;
  unsigned int value = number;
  while ((value & 0x80000000) == 0x00000000) {
    value = value << 1;
    shift++;
  }
  return shift;
}

/* negChecker - Checks if the given value is negatve or not.
 * Return the sign bit.
 */
int negChecker(tinysf_s value) {
  return (value << 19) >> 31;
}

/* getExp - Returns the exp alone in an unsigned int.
 * Return the exp.
 */
unsigned int getExp(tinysf_s value) {
  return ((value >> 8) << 28) >> 28;
}

/* getFrac - Returns the fraction alone in an unsigned int.
 * Return the fraction.
 */
unsigned int getFrac(tinysf_s value) {
  value = (value << 24);
  return value;
}

/* getE - Gets the E of the value by doing its exp - the bias.
 * Return the E.
 */
unsigned int getE(tinysf_s value) {
  return getExp(value) - BIAS;
}

/* infinityChecker - Checks if the given value is infinity or not.
 * Return 1 or 0 accordingly.
 */
int infinityChecker(tinysf_s value) {
  if (getExp(value) == 0xF) {
    if (getFrac(value) == 0x0) {
      return 1;
    }
  }
  return 0;
}

/* nanChecker - Checks if the given value is nan or not.
 * Return 1 or 0 accordingly.
 */
int nanChecker(tinysf_s value) {
  if (getExp(value) == 0xF) {
    if (getFrac(value) != 0x0) {
      return 1;
    }
  }
  return 0;
}

/* zeroChecker - Checks if the given value is zero or not.
 * Return 1 or 0 accordingly.
 */
int zeroChecker(tinysf_s value) {
  if (getExp(value) == 0x0) {
    if (getFrac(value) == 0x0) {
      return 1;
    }
  }
  return 0;
}

/* specialChecker - Also checks if the given value is a special case or not.
 * Return 1 if it is either infinity or nan, or 0 accordingly.
 */
int specialChecker(Number_s *number, tinysf_s value) {
  if (getExp(value) == 0xF) {
    if (getFrac(value) != 0x0) {
      number->is_nan = 1;
      number->is_infinity = 0;
    }
    else {
      number->is_infinity = 1;
      number->is_nan = 0;
    }
    return 1;
  }

  return 0;
}

/* rounder - Takes the given value and rounds it to 8 bits. This rounds to even.
 * Return The rounded tinysf value.
 */
tinysf_s rounder(tinysf_s value) {
  // Tracks the lsb, the first bit outside the main 8, and the others.
  tinysf_s lsb = value & 0x01000000;
  tinysf_s ninth = value & 0x00800000;
  tinysf_s leftovers = value & 0x007FFFFF;
  tinysf_s complete = value >> 24;
  if (ninth != 0) {
    if (leftovers != 0) {
      complete = complete + 0x00000001;
    }
    else {
      if (lsb != 0) {
        complete = complete + 0x00000001;
      }
    }
  }
  
  return complete;
}

/* addTinySF - Performs addition operations on two tinySF values
 * Return the resulting tinysf_s value.
 */
tinysf_s addTinySF(tinysf_s val1, tinysf_s val2) {
  // Starts our addition, negative, exptraSpaces, and difference
  //  trackers, as well as our final addition tinysf_s.
  int adder;
  int is_neg = 0;
  int extraSpaces = 0;
  int difference = 0;
  tinysf_s addition = 0x0;

  // Checks if there is an NaN.
  if ((nanChecker(val1) == 1) || (nanChecker(val2) == 1)) {
    return 0x00000FFF;
  }

  // Checks all the infinity cases, returning inf or -inf.
  else if ((infinityChecker(val1) == 1)) {
    if ((infinityChecker(val2) == 1)) {
      if (negChecker(val1) == 1) {
        if (negChecker(val2) == 1) {
          return 0x00001F00;
        }
        return 0x00000FFF;
      }
      else {
        if (negChecker(val2) == 1) {
          return 0x00000FFF;
        }
      }
      return 0x00000F00;
    }
    return 0x00000F00 + (val1 & 0x00001000);
  }

  // If the first value is X and the second is inf.
  else if ((infinityChecker(val2) == 1)) {
    return 0x00000F00 + (val2 & 0x00001000);
  }

  // Returns 0 or -0 respectively.
  else if ((zeroChecker(val1) == 1) && (zeroChecker(val2) == 1)) {
    if (negChecker(val1) == 1) {
        if (negChecker(val2) == 1) {
          return 0x00001000;
        }
      }
    return 0x00000000;
  }

  // Returns the other value if a 0 is present.
  else if ((zeroChecker(val1) == 1) && (zeroChecker(val2) != 1)) {
    return val2;
  }
  else if ((zeroChecker(val2) == 1) && (zeroChecker(val1) != 1)) {
    return val1;
  }

  // Returns 0 if they are identity
  if ((negateTinySF(val1) == val2) || (val1 == negateTinySF(val2)))  {
    return 0x00000000;
  }
  
  // This sets gets the first values E and mantissa.
  int e1 = getE(val1);
  tinysf_s m1 = (val1 & 0x000000FF) + 0x00000100;
  
  // This sets gets the second values E and mantissa.
  int e2 = getE(val2);
  tinysf_s m2 = (val2 & 0x000000FF) + 0x00000100; 
  int e;

  // Sets the proper values if the first values Exponent is larger. The 
  // mantissas are offset accordingly.
  if (e1 > e2) {
    e = e1;
    difference = e1 - e2;
    m1 = m1 << difference;
    m2 = (m2 << difference) >> (difference);
  }

  // Sets the proper values if the second values Exponent is larger. The 
  // mantissas are offset accordingly.
  else {
    e = e2;
    difference = e2 - e1;
    m1 = (m1 << difference) >> (difference);
    m2 = m2 << difference;
  }

  // This runs the correct addition or subtraction operation depending on what is inserted.
  if (negChecker(val1) == 1) {
    if (negChecker(val2) == 1) {
      adder = (-1 * (m1)) + ((m2) * -1);
    }
    else {
      adder = (-1 * (m1)) + (m2);
    }
  }
  else if (negChecker(val2) == 1) {
    adder = (m1) + (-1 * m2);
  }
  else {
    adder = (m1) + (m2);
  }
  
  // It is noted to be negative if it is.
  if(adder < 0) {
    is_neg = 1;
    adder = adder * -1;
  }
  // The mantissa is adjusted again and the e is fixed as well.
  tinysf_s mantissa = adder >> (8 + difference);
  if (mantissa >= 0x00000002) {
    while (mantissa != 0x00000001) {
      mantissa = mantissa >> 1;
      e++;
      extraSpaces++;
    }
  }

  // If the mantissa is less than 1, it is adjusted along with the e shrunk.
  else if ((mantissa < 0x00000001)) {
    mantissa = adder << (32 - (8 + difference) - 4);
    while (mantissa < 0x10000000) {
      mantissa = mantissa << 1;
      e--;
      extraSpaces--;
    }

    // NOTE: I know this part is basically a duplicate of what's below but I've 
    //       wasted so much time walking in wrong directions that if this works??
    //       IT WORKS.

    // The fraction is moved to the start, rounded, then put to the end.
    // The exp is appended in, and the negative is added if it exists.
    // There is one last catch for infinity and the value is returned.
    tinysf_s fraction = mantissa << 4;

    fraction = rounder(fraction);
    tinysf_s exp = (e + BIAS) << 8;
    fraction = fraction + exp;

    if (is_neg == 1) {
      fraction = fraction | 0x00001000;
    }

    if (e >= 8) {
      if (is_neg == 1) {
        return 0x00001F00;
      }
      return 0x00000F00;
    }
    return fraction;
  }

  // The fraction is moved to the start, rounded, then put to the end.
    // The exp is appended in, and the negative is added if it exists.
    // There is one last catch for infinity and the value is returned.
  tinysf_s fraction = (adder << leftZeroCounter(adder) + 1);

  fraction = rounder(fraction);
  tinysf_s exp = (e + BIAS) << 8;
  fraction = fraction + exp;

  if (is_neg == 1) {
    fraction = fraction | 0x00001000;
  }

  if (e >= 8) {
    if (is_neg == 1) {
      return 0x00001F00;
    }
    return 0x00000F00;
  }
  return fraction;
}

/* multTinySF - Performs multiplication operations on two tinySF values.
 * Return the resulting tinysf_s value.
 * 
 * NOTE: I made all of this one exclusively in hex without decimal operations
 *       because I am stupid. It WORKS THOUGH. IT WOOOOORKS!
 */
tinysf_s multTinySF(tinysf_s val1, tinysf_s val2){

  // These hold the number of extra spaces and the sign bit.
  int extraSpaces = 0;
  int sign = (val1 & 0x00001000) ^ (val2 & 0x00001000);

  // These are the special cases for NaN, zero, and infinity.
  if ((nanChecker(val1) == 1) || (nanChecker(val2) == 1)) {
    return 0x00000FFF;
  }
  else if ((infinityChecker(val1) == 1) || (infinityChecker(val2) == 1)) {
    if ((zeroChecker(val1) == 1) || (zeroChecker(val2) == 1)) {
      return 0x00000FFF;
    }
    return sign + 0x00000F00 | sign;
  }
  else if ((zeroChecker(val1) == 1) || (zeroChecker(val2) == 1)) {
    return sign + 0x00000700 | sign;
  }

  // We get the exponent and both mantissas.
  int exponent = getE(val1) + getE(val2);
  tinysf_s m1 = (val1 & 0x000000FF) + 0x00000100;
  tinysf_s m2 = (val2 & 0x000000FF) + 0x00000100;
  
  // We multiply them.
  tinysf_s mult = m1 * m2;
  tinysf_s fraction;
  tinysf_s exp;

  // And to get just our whole, we shift the frac bits out.
  tinysf_s mantissa = (mult >> (16));

  // Infinity if exp > 7.
  if (exponent >= 8) {
    return 0x00000F00 | sign;
  }

  // We count how many shifts are needed to make the whole 1.
  if (mantissa >= 0x00000002) {
    while (mantissa != 0x00000001) {
      mantissa = mantissa >> 1;
      exponent++;
      extraSpaces++;
    }

    // Check for infinity again.
    if (exponent >= 8) {
      return 0x00000F00 | sign;
    }
    
    // We round our fraction and shift it, putting in our exp too.
    fraction = mult << (16 - extraSpaces);
    exp = (exponent + 7) << 8;
    fraction = rounder(fraction);
    fraction =  fraction | exp;
    fraction = fraction | sign;
  }

  // If no shifts are needed.
  else if (mantissa == 0x00000001) {

    // We round our fraction and shift it, putting in our exp too.
    fraction = mult << (16 + extraSpaces);
    exp = (exponent + 7) << 8;

    fraction = rounder(fraction);
    fraction =  fraction | exp;
    fraction = fraction | sign;
  }
  // Does the same thing for smaller exponents
  else {
    // We round our fraction and shift it, putting in our exp too.
    fraction = mult << (16 + extraSpaces);
    exp = (exponent + 7) << 8;
    fraction = rounder(fraction);
    fraction =  fraction | exp;
    fraction = fraction | sign;
  }

  // 0 if it is too low.
  if (exponent <= -7) {
    return 0x00000000 | sign;
  }

  // Finally, return.
  return fraction;
}

/* isNegNum - Returns either the given value or its negative if is_negative is true..
 * Return the TINYSF Value a positive or negative.
 */
tinysf_s isNegNum(int is_negative, tinysf_s val) {
  if (is_negative == 1) {
    val = val | 0x00001000;
  }
  return val;
}


/* toTinySF - Converts a Number Struct (whole and fraction parts) into a TINYSF Value
 *  - number is managed by zuon, DO NOT FREE number.
 *  - Follow the project documentation for this function.
 * Return the TINYSF Value.
 */
tinysf_s toTinySF(Number_s *number) {
  // We intialize a final and temporary tinysf, exponent, and exp holder.
  tinysf_s final = 0x00000000;
  tinysf_s temp = 0x00000000;
  int exponent;
  unsigned int exp;

  // The negative bit is inserted.
  final = isNegNum(number->is_negative, final);
  // Check if Infinity
  if (number->is_infinity == 1) {
    if (number->is_nan != 1) {
      final = final | 0x00000F00;
      return final;
    }
  }
  // Check if NaN
  if (number->is_nan == 1) {
    final = final | 0x00000FFF;
    return final;
  }

  // Check if 0
  if (number->whole == 0) {
    if (number->fraction == 0) {
      return final;
    }
  }

  // ShiftWhole counts the empty bits needed to fit the whole.
  int shiftWhole = 32 - shiftCounter(number->whole);

  // If it is too big, infinity goes back.
  if (shiftWhole > 8) {
    final = final | 0x00000F00;
    return final;
  }

  // If it is within bounds, the e and exp are calculated and the space is made in 
  // the fraction.
  else if ((shiftWhole > 1 && shiftWhole < 9)) {
    exponent = shiftWhole - 1;
    exp = exponent + BIAS;

    final = number->fraction >> shiftWhole;
  }
  // If no shift is needed, the fraction and exp go in no problem.
  else if ((shiftWhole == 1) ) {
    exponent = 0;
    exp = exponent + BIAS;
    final = number->fraction;
  }

  // If too many frac bits need to move instead, there's an underflow and 0 
  // returns. Otherwise the shifts are made.
  else {
    exponent = -(leftZeroCounter(number->fraction) + 1);
    if (exponent <= -8 && (leftZeroCounter(number->fraction) >= -exponent)) {
      final = 0x0;
      final = isNegNum(number->is_negative, final);
      return final;
    }
    exp = exponent + BIAS;
    final = number->fraction;
    
  }
  
  // Infiinity returns if exp is too big.
  if (exp >= 0xF) {
    
    final = 0x00000F00;
    final = isNegNum(number->is_negative, final);
    return final;
    //RETUNS INFINITY
  }

  // Underflow from exp is caught again here.
  if (exp <= 0 && (leftZeroCounter(number->fraction) >= -exp)) {
    final = 0x0;
    final = isNegNum(number->is_negative, final);
    return final;
  }

  else {
    if (exponent <= 0) {
      // The shifts to the left are made.
      final = final << (-exponent);
    }
    else {
      // The shifts to the right are made.
      temp = (number->whole);
      temp = temp << (32 - exponent);
      final = (number->fraction >> exponent) | temp;
    }
    
    // The value is rounded!
    final = rounder(final);
    
    // Overflow into the exp is corrected accordingly.
    if (final >= 0x00000100) {
      if (exp < 0x0000000e) {
        exp += 0x00000001;
      }
      else {
        final = 0x00000F00;
        final = isNegNum(number->is_negative, final);
        return final;
      }
    }
    // It's appended.
    final = (final & 0xFFFFF0FF) | (exp << 8);
    
  }
  // The sign is for sure in it now :)...
  final = isNegNum(number->is_negative, final);
  return final;
}

/* toNumber - Converts a TINYSF Value into a Number Struct (whole and fraction parts)
 *  - number is managed by zuon, DO NOT FREE or re-Allocate number.
 *    - (It is already allocated)
 *  - Follow the project documentation for this function.
 * Return 0.
 */
int toNumber(Number_s *number, tinysf_s value) {
  // We initialize holders for the E and the mantissa.
  int e;
  unsigned int mantissa;

  // We set the proper negative flag.
  number->is_negative = negChecker(value);

  // Returns infinity, Nan, or keeps going.
  if (specialChecker(number, value) == 1) {
    return 0;
  }

  // Calculates the exponent.
  e = getExp(value) - BIAS;

  // for Es greater than 0:
  if ((e > 0)) {
    // We hold our mantissa.
    mantissa = (getFrac(value) >> 1) + 0x80000000;

    // We store the whole number and fraction, shifting by our exponent.
    number->whole = (0x00000001 << e) | (getFrac(value) >> (32 - e));
    number->fraction = getFrac(value) << (e);
  }

  // If it is 0, we make it one with the original frac.
  else if (e == 0) {
    mantissa = (getFrac(value) >> 1) + 0x80000000;

    number->whole = 1;
    number->fraction = mantissa << 1;
  }

  // If it's negative and within bounds, we do the same as before.
  else if (e < 0 && e > -BIAS) {
    mantissa = (getFrac(value) >> 1) + 0x80000000;
    
    number->whole = 0;
    number->fraction = mantissa >> (-e - 1);
  }
  // And if it is 0, then 0.
  else {
    number->whole = 0;
    number->fraction = 0;
  }

  // Finished!
  return 0; 
}

/* opTinySF - Performs an operation on two tinySF values
 *  - Follow the project documentation for this function.
 * Return the resulting tinysf_s value or -1 if operator was invalid.
 */
tinysf_s opTinySF(char operator, tinysf_s val1, tinysf_s val2) {
  // This calls either add or multTinySF for the correct operations.
  if (operator == '+') {
    return addTinySF(val1, val2);
  }
  else if (operator == '-') {
    return addTinySF(val1, negateTinySF(val2));
  }
  else if (operator == '*') {
    return multTinySF(val1, val2);
  }
  return -1; // Replace this Line with your Code!
}

/* negateTINYSF - Negates a TINYSF Value.
 *  - Follow the project documentation for this function.
 * Return the resulting TINYSF Value
 */
tinysf_s negateTinySF(tinysf_s value) {
  value = value ^ 0x00001000;
  return value; 
}
