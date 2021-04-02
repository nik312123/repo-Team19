/*
 * File name:
 * FractionTest.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Tests the Fraction class
 */

package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class FractionTest {
    
    private FractionTest() {}
    
    //getNumerator and getDenominator are also adequately tested here in addition to the constructor
    @Test
    void testConstructor() {
        Assertions.assertAll(
            //Testing division by 0 error with 0 as numerator
            () -> Assertions.assertThrows(ArithmeticException.class, () -> new Fraction(0, 0)),
            //Testing division by 0 with a positive integer
            () -> Assertions.assertThrows(ArithmeticException.class, () -> new Fraction(3, 0)),
            //Testing division by 0 with a negative integer
            () -> Assertions.assertThrows(ArithmeticException.class, () -> new Fraction(-3, 0)),
            
            //Testing that 2/2 is reduced to 1/1
            () -> Assertions.assertEquals(1, new Fraction(2, 2).getNumerator()),
            () -> Assertions.assertEquals(1, new Fraction(2, 2).getDenominator()),
            //Testing that 4/2 is reduced to 2/1
            () -> Assertions.assertEquals(2, new Fraction(4, 2).getNumerator()),
            () -> Assertions.assertEquals(1, new Fraction(4, 2).getDenominator()),
            //Testing that 2/4 is reduced to 1/2
            () -> Assertions.assertEquals(1, new Fraction(2, 4).getNumerator()),
            () -> Assertions.assertEquals(2, new Fraction(2, 4).getDenominator()),
            //Testing that 4/-10 is reduced to -2/5
            () -> Assertions.assertEquals(-2, new Fraction(4, -10).getNumerator()),
            () -> Assertions.assertEquals(5, new Fraction(4, -10).getDenominator()),
            //Testing that 0/5 is reduced to 0/1
            () -> Assertions.assertEquals(0, new Fraction(0, 5).getNumerator()),
            () -> Assertions.assertEquals(1, new Fraction(0, 5).getDenominator()),
            
            //Testing that -2/9 reduces to -2/9 (the same)
            () -> Assertions.assertEquals(-2, new Fraction(-2, 9).getNumerator()),
            () -> Assertions.assertEquals(9, new Fraction(-2, 9).getDenominator()),
            //Testing that 2/-9 reduces to -2/9
            () -> Assertions.assertEquals(-2, new Fraction(2, -9).getNumerator()),
            () -> Assertions.assertEquals(9, new Fraction(2, -9).getDenominator()),
            //Testing that -2/-9 reduces to 2/9
            () -> Assertions.assertEquals(2, new Fraction(-2, -9).getNumerator()),
            () -> Assertions.assertEquals(9, new Fraction(-2, -9).getDenominator())
        );
    }
    
    @Test
    void testGcd() {
        Assertions.assertAll(
            //Testing general case
            () -> Assertions.assertEquals(120, Fraction.gcd(1080, 1920)),
            
            //Testing n1 = n2
            () -> Assertions.assertEquals(13, Fraction.gcd(13, 13)),
            
            //Testing n1 or n2 is prime
            () -> Assertions.assertEquals(1, Fraction.gcd(29, 900)),
            () -> Assertions.assertEquals(1, Fraction.gcd(900, 29)),
            
            //Testing n1 or n2 is a multiple of the other
            () -> Assertions.assertEquals(20, Fraction.gcd(100, 20)),
            () -> Assertions.assertEquals(20, Fraction.gcd(20, 100)),
            
            //Testing the case where one of n1 and n2 is 0
            () -> Assertions.assertEquals(5, Fraction.gcd(5, 0)),
            () -> Assertions.assertEquals(5, Fraction.gcd(0, 5)),
            
            //Testing the case where one of n1 and n2 is 1
            () -> Assertions.assertEquals(1, Fraction.gcd(5, 1)),
            () -> Assertions.assertEquals(1, Fraction.gcd(1, 5)),
            
            //Testing the property gcd(n1, n2) = gcd(|n1 - n2|, min(n1, n2)) when n1 and n2 are odd
            () -> Assertions.assertEquals(Fraction.gcd(Math.abs(121 - 37), Math.min(121, 37)), Fraction.gcd(121, 37)),
            
            //Testing the property gcd(n1, n2) = 2 * gcd(n1/2, n2/2) when n1 and n2 are even
            () -> Assertions.assertEquals(2 * Fraction.gcd(19, 164), Fraction.gcd(2 * 19, 2 * 164)),
            
            //Testing the property gcd(n1, n2) = gcd(n1/2, n2) when n1 is even and n2 is odd
            () -> Assertions.assertEquals(Fraction.gcd(2 * 177, 159), Fraction.gcd(177, 159)),
            
            //Testing the property gcd(n1, n2) = gcd(n1, n2/2) when n1 is odd and n2 is even
            () -> Assertions.assertEquals(Fraction.gcd(193, 2 * 123), Fraction.gcd(193, 123))
        );
    }
    
    @Test
    void testReciprocal() {
        final Fraction twoNinths = new Fraction(2, 9);
        final Fraction fiveFourths = new Fraction(5, 4);
        final Fraction negativeSixSevenths = new Fraction(-6, 7);
        final Fraction negativeSevenSixths = new Fraction(-7, 6);
        final Fraction one = new Fraction(1, 1);
        final Fraction zero = new Fraction(0, 1);
        
        //Testing the reciprocals of the above fractions
        Assertions.assertAll(
            () -> Assertions.assertEquals(new Fraction(9, 2), twoNinths.reciprocal()),
            () -> Assertions.assertEquals(new Fraction(4, 5), fiveFourths.reciprocal()),
            () -> Assertions.assertEquals(new Fraction(-7, 6), negativeSixSevenths.reciprocal()),
            () -> Assertions.assertEquals(new Fraction(-6, 7), negativeSevenSixths.reciprocal()),
            () -> Assertions.assertEquals(one, one.reciprocal()),
            //Special case: the reciprocal of 0 will result in an exception being thrown
            () -> Assertions.assertThrows(ArithmeticException.class, zero::reciprocal)
        );
    }
    
    @Test
    void testGetWholePart() {
        final Fraction twoNinths = new Fraction(2, 9);
        final Fraction fiveFourths = new Fraction(5, 4);
        final Fraction negativeSixSevenths = new Fraction(-6, 7);
        final Fraction negativeSevenSixths = new Fraction(-7, 6);
        final Fraction one = new Fraction(1, 1);
        final Fraction zero = new Fraction(0, 1);
        
        //Testing the whole parts of the above fractions
        Assertions.assertAll(
            () -> Assertions.assertEquals(0, twoNinths.getWholePart()),
            () -> Assertions.assertEquals(1, fiveFourths.getWholePart()),
            () -> Assertions.assertEquals(0, negativeSixSevenths.getWholePart()),
            () -> Assertions.assertEquals(-1, negativeSevenSixths.getWholePart()),
            () -> Assertions.assertEquals(1, one.getWholePart()),
            () -> Assertions.assertEquals(0, zero.getWholePart())
        );
    }
    
    @Test
    void testGetFractionalPart() {
        final Fraction twoNinths = new Fraction(2, 9);
        final Fraction fiveFourths = new Fraction(5, 4);
        final Fraction negativeSixSevenths = new Fraction(-6, 7);
        final Fraction negativeSevenSixths = new Fraction(-7, 6);
        final Fraction one = new Fraction(1, 1);
        final Fraction zero = new Fraction(0, 1);
        
        //Testing the fractional parts of the above fractions
        Assertions.assertAll(
            () -> Assertions.assertEquals(twoNinths, twoNinths.getFractionalPart()),
            () -> Assertions.assertEquals(new Fraction(1, 4), fiveFourths.getFractionalPart()),
            () -> Assertions.assertEquals(negativeSixSevenths, negativeSixSevenths.getFractionalPart()),
            () -> Assertions.assertEquals(new Fraction(-1, 6), negativeSevenSixths.getFractionalPart()),
            () -> Assertions.assertEquals(zero, one.getFractionalPart()),
            () -> Assertions.assertEquals(zero, zero.getFractionalPart())
        );
    }
    
    //Test that two doubles are essentially equal (within epsilon of each other)
    private boolean doubleEqualsEpsilon(final double d1, final double d2, final double epsilon) {
        return Math.abs(d2 - d1) <= epsilon;
    }
    
    @Test
    void testGetDoubleValue() {
        final Fraction twoNinths = new Fraction(2, 9);
        final Fraction fiveFourths = new Fraction(5, 4);
        final Fraction negativeSixSevenths = new Fraction(-6, 7);
        final Fraction negativeSevenSixths = new Fraction(-7, 6);
        final Fraction one = new Fraction(1, 1);
        final Fraction zero = new Fraction(0, 1);
        
        //Epsilon used to determine that two doubles are essentially equal (within epsilon)
        final double epsilon = 1E-6;
        
        //Testing the double values of the above fractions
        final String doubleComparisonFailFormatStr = "Attempted comparison between %s and %d.0 / %d";
        Assertions.assertAll(
            () -> Assertions.assertTrue(
                doubleEqualsEpsilon(twoNinths.getDoubleValue(), 2.0 / 9, epsilon),
                String.format(doubleComparisonFailFormatStr, twoNinths, 2, 9)
            ),
            () -> Assertions.assertTrue(
                doubleEqualsEpsilon(fiveFourths.getDoubleValue(), 5.0 / 4, epsilon),
                String.format(doubleComparisonFailFormatStr, fiveFourths, 5, 4)
            ),
            () -> Assertions.assertTrue(
                doubleEqualsEpsilon(negativeSixSevenths.getDoubleValue(), -6.0 / 7, epsilon),
                String.format(doubleComparisonFailFormatStr, negativeSixSevenths, -6, 7)
            ),
            () -> Assertions.assertTrue(doubleEqualsEpsilon(
                negativeSevenSixths.getDoubleValue(), -7.0 / 6, epsilon),
                String.format(doubleComparisonFailFormatStr, negativeSevenSixths, -7, 6)
            ),
            () -> Assertions.assertEquals(one.getDoubleValue(), 1.0),
            () -> Assertions.assertEquals(zero.getDoubleValue(), 0.0)
        );
    }
    
    @Test
    void testAdd() {
        final Fraction twoNinths = new Fraction(2, 9);
        final Fraction fiveFourths = new Fraction(5, 4);
        final Fraction negativeSixSevenths = new Fraction(-6, 7);
        final Fraction negativeSevenSixths = new Fraction(-7, 6);
        final Fraction one = new Fraction(1, 1);
        final Fraction zero = new Fraction(0, 1);
        
        /*
         * Checking sums of all combinations of the above fractions, using Python's Fraction class as a method to generate the sums of all
         * combinations of the above, with zero being included as the identity for addition
         */
        Assertions.assertAll(
            () -> Assertions.assertEquals(new Fraction(4, 9), twoNinths.add(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(53, 36), twoNinths.add(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(-40, 63), twoNinths.add(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(-17, 18), twoNinths.add(negativeSevenSixths)),
            () -> Assertions.assertEquals(new Fraction(11, 9), twoNinths.add(one)),
            //Special case: addition with identity
            () -> Assertions.assertEquals(twoNinths, twoNinths.add(zero)),
            () -> Assertions.assertEquals(new Fraction(53, 36), fiveFourths.add(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(5, 2), fiveFourths.add(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(11, 28), fiveFourths.add(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(1, 12), fiveFourths.add(negativeSevenSixths)),
            () -> Assertions.assertEquals(new Fraction(9, 4), fiveFourths.add(one)),
            //Special case: addition with identity
            () -> Assertions.assertEquals(fiveFourths, fiveFourths.add(zero)),
            () -> Assertions.assertEquals(new Fraction(-40, 63), negativeSixSevenths.add(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(11, 28), negativeSixSevenths.add(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(-12, 7), negativeSixSevenths.add(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(-85, 42), negativeSixSevenths.add(negativeSevenSixths)),
            () -> Assertions.assertEquals(new Fraction(1, 7), negativeSixSevenths.add(one)),
            //Special case: addition with identity
            () -> Assertions.assertEquals(negativeSixSevenths, negativeSixSevenths.add(zero)),
            () -> Assertions.assertEquals(new Fraction(-17, 18), negativeSevenSixths.add(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(1, 12), negativeSevenSixths.add(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(-85, 42), negativeSevenSixths.add(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(-7, 3), negativeSevenSixths.add(negativeSevenSixths)),
            () -> Assertions.assertEquals(new Fraction(-1, 6), negativeSevenSixths.add(one)),
            //Special case: addition with identity
            () -> Assertions.assertEquals(negativeSevenSixths, negativeSevenSixths.add(zero)),
            () -> Assertions.assertEquals(new Fraction(11, 9), one.add(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(9, 4), one.add(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(1, 7), one.add(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(-1, 6), one.add(negativeSevenSixths)),
            () -> Assertions.assertEquals(new Fraction(2, 1), one.add(one)),
            //Special case: addition with identity
            () -> Assertions.assertEquals(one, one.add(zero)),
            //Special case: addition with identity
            () -> Assertions.assertEquals(twoNinths, zero.add(twoNinths)),
            //Special case: addition with identity
            () -> Assertions.assertEquals(fiveFourths, zero.add(fiveFourths)),
            //Special case: addition with identity
            () -> Assertions.assertEquals(negativeSixSevenths, zero.add(negativeSixSevenths)),
            //Special case: addition with identity
            () -> Assertions.assertEquals(negativeSevenSixths, zero.add(negativeSevenSixths)),
            //Special case: addition with identity
            () -> Assertions.assertEquals(one, zero.add(one)),
            //Special case: addition with identity
            () -> Assertions.assertEquals(zero, zero.add(zero))
        );
    }
    
    @Test
    void testSubtract() {
        final Fraction twoNinths = new Fraction(2, 9);
        final Fraction fiveFourths = new Fraction(5, 4);
        final Fraction negativeSixSevenths = new Fraction(-6, 7);
        final Fraction negativeSevenSixths = new Fraction(-7, 6);
        final Fraction one = new Fraction(1, 1);
        final Fraction zero = new Fraction(0, 1);
        
        /*
         * Checking sums of all combinations of the above fractions, using Python's Fraction class as a method to generate the differences of all
         * combinations of the above, with zero being included as the identity for subtraction
         */
        Assertions.assertAll(
            //Special case: subtraction with self
            () -> Assertions.assertEquals(zero, twoNinths.subtract(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(-37, 36), twoNinths.subtract(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(68, 63), twoNinths.subtract(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(25, 18), twoNinths.subtract(negativeSevenSixths)),
            () -> Assertions.assertEquals(new Fraction(-7, 9), twoNinths.subtract(one)),
            //Special case: subtraction with identity
            () -> Assertions.assertEquals(twoNinths, twoNinths.subtract(zero)),
            () -> Assertions.assertEquals(new Fraction(37, 36), fiveFourths.subtract(twoNinths)),
            //Special case: subtraction with self
            () -> Assertions.assertEquals(zero, fiveFourths.subtract(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(59, 28), fiveFourths.subtract(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(29, 12), fiveFourths.subtract(negativeSevenSixths)),
            () -> Assertions.assertEquals(new Fraction(1, 4), fiveFourths.subtract(one)),
            //Special case: subtraction with identity
            () -> Assertions.assertEquals(fiveFourths, fiveFourths.subtract(zero)),
            () -> Assertions.assertEquals(new Fraction(-68, 63), negativeSixSevenths.subtract(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(-59, 28), negativeSixSevenths.subtract(fiveFourths)),
            //Special case: subtraction with self
            () -> Assertions.assertEquals(zero, negativeSixSevenths.subtract(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(13, 42), negativeSixSevenths.subtract(negativeSevenSixths)),
            () -> Assertions.assertEquals(new Fraction(-13, 7), negativeSixSevenths.subtract(one)),
            //Special case: subtraction with identity
            () -> Assertions.assertEquals(negativeSixSevenths, negativeSixSevenths.subtract(zero)),
            () -> Assertions.assertEquals(new Fraction(-25, 18), negativeSevenSixths.subtract(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(-29, 12), negativeSevenSixths.subtract(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(-13, 42), negativeSevenSixths.subtract(negativeSixSevenths)),
            //Special case: subtraction with self
            () -> Assertions.assertEquals(zero, negativeSevenSixths.subtract(negativeSevenSixths)),
            () -> Assertions.assertEquals(new Fraction(-13, 6), negativeSevenSixths.subtract(one)),
            //Special case: subtraction with identity
            () -> Assertions.assertEquals(negativeSevenSixths, negativeSevenSixths.subtract(zero)),
            () -> Assertions.assertEquals(new Fraction(7, 9), one.subtract(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(-1, 4), one.subtract(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(13, 7), one.subtract(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(13, 6), one.subtract(negativeSevenSixths)),
            //Special case: subtraction with self
            () -> Assertions.assertEquals(zero, one.subtract(one)),
            //Special case: subtraction with identity
            () -> Assertions.assertEquals(one, one.subtract(zero)),
            () -> Assertions.assertEquals(new Fraction(-2, 9), zero.subtract(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(-5, 4), zero.subtract(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(6, 7), zero.subtract(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(7, 6), zero.subtract(negativeSevenSixths)),
            () -> Assertions.assertEquals(new Fraction(-1, 1), zero.subtract(one)),
            //Special case: subtraction with identity, subtraction with self
            () -> Assertions.assertEquals(zero, zero.subtract(zero))
        );
    }
    
    @Test
    void testMultiply() {
        final Fraction twoNinths = new Fraction(2, 9);
        final Fraction fiveFourths = new Fraction(5, 4);
        final Fraction negativeSixSevenths = new Fraction(-6, 7);
        final Fraction negativeSevenSixths = new Fraction(-7, 6);
        final Fraction one = new Fraction(1, 1);
        final Fraction zero = new Fraction(0, 1);
        
        /*
         * Checking sums of all combinations of the above fractions, using Python's Fraction class as a method to generate the products of all
         * combinations of the above, with one being included as the identity for multiplication
         */
        Assertions.assertAll(
            () -> Assertions.assertEquals(new Fraction(4, 81), twoNinths.multiply(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(5, 18), twoNinths.multiply(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(-4, 21), twoNinths.multiply(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(-7, 27), twoNinths.multiply(negativeSevenSixths)),
            //Special case: multiplication with identity
            () -> Assertions.assertEquals(twoNinths, twoNinths.multiply(one)),
            //Special case: multiplication with 0
            () -> Assertions.assertEquals(zero, twoNinths.multiply(zero)),
            () -> Assertions.assertEquals(new Fraction(5, 18), fiveFourths.multiply(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(25, 16), fiveFourths.multiply(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(-15, 14), fiveFourths.multiply(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(-35, 24), fiveFourths.multiply(negativeSevenSixths)),
            //Special case: multiplication with identity
            () -> Assertions.assertEquals(fiveFourths, fiveFourths.multiply(one)),
            //Special case: multiplication with 0
            () -> Assertions.assertEquals(zero, fiveFourths.multiply(zero)),
            () -> Assertions.assertEquals(new Fraction(-4, 21), negativeSixSevenths.multiply(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(-15, 14), negativeSixSevenths.multiply(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(36, 49), negativeSixSevenths.multiply(negativeSixSevenths)),
            //Special case: multiplication with reciprocal
            () -> Assertions.assertEquals(one, negativeSixSevenths.multiply(negativeSevenSixths)),
            //Special case: multiplication with identity
            () -> Assertions.assertEquals(negativeSixSevenths, negativeSixSevenths.multiply(one)),
            //Special case: multiplication with 0
            () -> Assertions.assertEquals(zero, negativeSixSevenths.multiply(zero)),
            () -> Assertions.assertEquals(new Fraction(-7, 27), negativeSevenSixths.multiply(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(-35, 24), negativeSevenSixths.multiply(fiveFourths)),
            //Special case: multiplication with reciprocal
            () -> Assertions.assertEquals(one, negativeSevenSixths.multiply(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(49, 36), negativeSevenSixths.multiply(negativeSevenSixths)),
            //Special case: multiplication with identity
            () -> Assertions.assertEquals(negativeSevenSixths, negativeSevenSixths.multiply(one)),
            //Special case: multiplication with 0
            () -> Assertions.assertEquals(zero, negativeSevenSixths.multiply(zero)),
            //Special case: multiplication with identity
            () -> Assertions.assertEquals(twoNinths, one.multiply(twoNinths)),
            //Special case: multiplication with identity
            () -> Assertions.assertEquals(fiveFourths, one.multiply(fiveFourths)),
            //Special case: multiplication with identity
            () -> Assertions.assertEquals(negativeSixSevenths, one.multiply(negativeSixSevenths)),
            //Special case: multiplication with identity
            () -> Assertions.assertEquals(negativeSevenSixths, one.multiply(negativeSevenSixths)),
            //Special case: multiplication with identity
            () -> Assertions.assertEquals(one, one.multiply(one)),
            //Special case: multiplication with identity, multiplication with 0
            () -> Assertions.assertEquals(zero, one.multiply(zero)),
            //Special case: multiplication with 0
            () -> Assertions.assertEquals(zero, zero.multiply(twoNinths)),
            //Special case: multiplication with 0
            () -> Assertions.assertEquals(zero, zero.multiply(fiveFourths)),
            //Special case: multiplication with 0
            () -> Assertions.assertEquals(zero, zero.multiply(negativeSixSevenths)),
            //Special case: multiplication with 0
            () -> Assertions.assertEquals(zero, zero.multiply(negativeSevenSixths)),
            //Special case: multiplication with 0
            () -> Assertions.assertEquals(zero, zero.multiply(one)),
            //Special case: multiplication with 0
            () -> Assertions.assertEquals(zero, zero.multiply(zero))
        );
    }
    
    @Test
    void testDivide() {
        final Fraction twoNinths = new Fraction(2, 9);
        final Fraction fiveFourths = new Fraction(5, 4);
        final Fraction negativeSixSevenths = new Fraction(-6, 7);
        final Fraction negativeSevenSixths = new Fraction(-7, 6);
        final Fraction one = new Fraction(1, 1);
        final Fraction zero = new Fraction(0, 1);
        
        /*
         * Checking sums of all combinations of the above fractions, using Python's Fraction class as a method to generate the quotients of all
         * combinations of the above, with one being included as the identity for subtraction and zero being included as a special case as division
         * by zero results in an exception
         */
        Assertions.assertAll(
            //Special case: division by self (when nonzero)
            () -> Assertions.assertEquals(one, twoNinths.divide(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(8, 45), twoNinths.divide(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(-7, 27), twoNinths.divide(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(-4, 21), twoNinths.divide(negativeSevenSixths)),
            //Special case: division by 1
            () -> Assertions.assertEquals(twoNinths, twoNinths.divide(one)),
            //Special case: division by 0
            () -> Assertions.assertThrows(ArithmeticException.class, () -> twoNinths.divide(zero)),
            () -> Assertions.assertEquals(new Fraction(45, 8), fiveFourths.divide(twoNinths)),
            //Special case: division by self (when nonzero)
            () -> Assertions.assertEquals(one, fiveFourths.divide(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(-35, 24), fiveFourths.divide(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(-15, 14), fiveFourths.divide(negativeSevenSixths)),
            //Special case: division by 1
            () -> Assertions.assertEquals(fiveFourths, fiveFourths.divide(one)),
            //Special case: division by 0
            () -> Assertions.assertThrows(ArithmeticException.class, () -> fiveFourths.divide(zero)),
            () -> Assertions.assertEquals(new Fraction(-27, 7), negativeSixSevenths.divide(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(-24, 35), negativeSixSevenths.divide(fiveFourths)),
            //Special case: division by self (when nonzero)
            () -> Assertions.assertEquals(one, negativeSixSevenths.divide(negativeSixSevenths)),
            () -> Assertions.assertEquals(new Fraction(36, 49), negativeSixSevenths.divide(negativeSevenSixths)),
            //Special case: division by 1
            () -> Assertions.assertEquals(negativeSixSevenths, negativeSixSevenths.divide(one)),
            //Special case: division by 0
            () -> Assertions.assertThrows(ArithmeticException.class, () -> negativeSixSevenths.divide(zero)),
            () -> Assertions.assertEquals(new Fraction(-21, 4), negativeSevenSixths.divide(twoNinths)),
            () -> Assertions.assertEquals(new Fraction(-14, 15), negativeSevenSixths.divide(fiveFourths)),
            () -> Assertions.assertEquals(new Fraction(49, 36), negativeSevenSixths.divide(negativeSixSevenths)),
            //Special case: division by self (when nonzero)
            () -> Assertions.assertEquals(one, negativeSevenSixths.divide(negativeSevenSixths)),
            () -> Assertions.assertEquals(negativeSevenSixths, negativeSevenSixths.divide(one)),
            //Special case: division by 0
            () -> Assertions.assertThrows(ArithmeticException.class, () -> negativeSevenSixths.divide(zero)),
            //Special case: 1 divided by a fraction is equivalent to its reciprocal (when nonzero)
            () -> Assertions.assertEquals(twoNinths.reciprocal(), one.divide(twoNinths)),
            //Special case: 1 divided by a fraction is equivalent to its reciprocal (when nonzero)
            () -> Assertions.assertEquals(fiveFourths.reciprocal(), one.divide(fiveFourths)),
            //Special case: 1 divided by a fraction is equivalent to its reciprocal (when nonzero)
            () -> Assertions.assertEquals(negativeSixSevenths.reciprocal(), one.divide(negativeSixSevenths)),
            //Special case: 1 divided by a fraction is equivalent to its reciprocal (when nonzero)
            () -> Assertions.assertEquals(negativeSevenSixths.reciprocal(), one.divide(negativeSevenSixths)),
            //Special case: division by self (when nonzero), division by 1, 1 divided by a fraction is equivalent to its reciprocal (when nonzero)
            () -> Assertions.assertEquals(one, one.divide(one)),
            //Special case: division by 0
            () -> Assertions.assertThrows(ArithmeticException.class, () -> one.divide(zero)),
            //Special case: 0 divided by anything nonzero
            () -> Assertions.assertEquals(zero, zero.divide(twoNinths)),
            //Special case: 0 divided by anything nonzero
            () -> Assertions.assertEquals(zero, zero.divide(fiveFourths)),
            //Special case: 0 divided by anything nonzero
            () -> Assertions.assertEquals(zero, zero.divide(negativeSixSevenths)),
            //Special case: 0 divided by anything nonzero
            () -> Assertions.assertEquals(zero, zero.divide(negativeSevenSixths)),
            //Special case: 0 divided by anything nonzero, division by 1
            () -> Assertions.assertEquals(zero, zero.divide(one)),
            //Special case: division by 0
            () -> Assertions.assertThrows(ArithmeticException.class, () -> zero.divide(zero))
        );
        
    }
    
    @Test
    void testCompare() {
        final Fraction twoNinths = new Fraction(2, 9);
        final Fraction fiveFourths = new Fraction(5, 4);
        final Fraction negativeSixSevenths = new Fraction(-6, 7);
        final Fraction negativeSevenSixths = new Fraction(-7, 6);
        final Fraction one = new Fraction(1, 1);
        final Fraction zero = new Fraction(0, 1);
        
        /*
         * Checking comparisons of all combinations of the above fractions, using Python's Fraction class as a method to generate the quotients of all
         * combinations of the above with each fraction being compared to itself for the equality (0) case
         */
        Assertions.assertAll(
            //Special case: comparison against oneself
            () -> Assertions.assertEquals(0, twoNinths.compareTo(twoNinths)),
            () -> Assertions.assertEquals(-1, twoNinths.compareTo(fiveFourths)),
            () -> Assertions.assertEquals(1, twoNinths.compareTo(negativeSixSevenths)),
            () -> Assertions.assertEquals(1, twoNinths.compareTo(negativeSevenSixths)),
            () -> Assertions.assertEquals(-1, twoNinths.compareTo(one)),
            () -> Assertions.assertEquals(1, twoNinths.compareTo(zero)),
            () -> Assertions.assertEquals(1, fiveFourths.compareTo(twoNinths)),
            //Special case: comparison against oneself
            () -> Assertions.assertEquals(0, fiveFourths.compareTo(fiveFourths)),
            () -> Assertions.assertEquals(1, fiveFourths.compareTo(negativeSixSevenths)),
            () -> Assertions.assertEquals(1, fiveFourths.compareTo(negativeSevenSixths)),
            () -> Assertions.assertEquals(1, fiveFourths.compareTo(one)),
            () -> Assertions.assertEquals(1, fiveFourths.compareTo(zero)),
            () -> Assertions.assertEquals(-1, negativeSixSevenths.compareTo(twoNinths)),
            () -> Assertions.assertEquals(-1, negativeSixSevenths.compareTo(fiveFourths)),
            //Special case: comparison against oneself
            () -> Assertions.assertEquals(0, negativeSixSevenths.compareTo(negativeSixSevenths)),
            () -> Assertions.assertEquals(1, negativeSixSevenths.compareTo(negativeSevenSixths)),
            () -> Assertions.assertEquals(-1, negativeSixSevenths.compareTo(one)),
            () -> Assertions.assertEquals(-1, negativeSixSevenths.compareTo(zero)),
            () -> Assertions.assertEquals(-1, negativeSevenSixths.compareTo(twoNinths)),
            () -> Assertions.assertEquals(-1, negativeSevenSixths.compareTo(fiveFourths)),
            () -> Assertions.assertEquals(-1, negativeSevenSixths.compareTo(negativeSixSevenths)),
            //Special case: comparison against oneself
            () -> Assertions.assertEquals(0, negativeSevenSixths.compareTo(negativeSevenSixths)),
            () -> Assertions.assertEquals(-1, negativeSevenSixths.compareTo(one)),
            () -> Assertions.assertEquals(-1, negativeSevenSixths.compareTo(zero)),
            () -> Assertions.assertEquals(1, one.compareTo(twoNinths)),
            () -> Assertions.assertEquals(-1, one.compareTo(fiveFourths)),
            () -> Assertions.assertEquals(1, one.compareTo(negativeSixSevenths)),
            () -> Assertions.assertEquals(1, one.compareTo(negativeSevenSixths)),
            //Special case: comparison against oneself
            () -> Assertions.assertEquals(0, one.compareTo(one)),
            () -> Assertions.assertEquals(1, one.compareTo(zero)),
            () -> Assertions.assertEquals(-1, zero.compareTo(twoNinths)),
            () -> Assertions.assertEquals(-1, zero.compareTo(fiveFourths)),
            () -> Assertions.assertEquals(1, zero.compareTo(negativeSixSevenths)),
            () -> Assertions.assertEquals(1, zero.compareTo(negativeSevenSixths)),
            () -> Assertions.assertEquals(-1, zero.compareTo(one)),
            //Special case: comparison against oneself
            () -> Assertions.assertEquals(0, zero.compareTo(zero))
        );
    }
    
    @Test
    void testEquals() {
        Assertions.assertAll(
            //Testing the identity with 1/2
            () -> Assertions.assertEquals(new Fraction(1, 2), new Fraction(1, 2)),
            //Testing the identity with 2/1
            () -> Assertions.assertEquals(new Fraction(2, 1), new Fraction(2, 1)),
            //Testing the identity with 1/1
            () -> Assertions.assertEquals(new Fraction(1, 1), new Fraction(1, 1)),
            //Testing the identity with -2/9
            () -> Assertions.assertEquals(new Fraction(-2, 9), new Fraction(-2, 9)),
            //Testing the identity with -9/4
            () -> Assertions.assertEquals(new Fraction(-9, 4), new Fraction(-9, 4)),
            
            //Testing that two fractions with the same numerators but different denominators are not equal
            () -> Assertions.assertNotEquals(new Fraction(2, 3), new Fraction(2, 9)),
            //Testing that two fractions with the same denominators but different numerators are not equal
            () -> Assertions.assertNotEquals(new Fraction(3, 2), new Fraction(9, 2)),
            
            //Confirms the inequality of a fraction and its negative form
            () -> Assertions.assertNotEquals(new Fraction(2, 9), new Fraction(-2, 9))
        );
    }
    
    @Test
    void testToString() {
        final Fraction twoNinths = new Fraction(2, 9);
        final Fraction negativeSevenSixths = new Fraction(-7, 6);
        
        Assertions.assertAll(
            //Testing the representation of a standard fraction
            () -> Assertions.assertEquals("2 / 9", twoNinths.toString()),
            //Testing the representation of a negative fraction
            () -> Assertions.assertEquals("-7 / 6", negativeSevenSixths.toString())
        );
    }
}
