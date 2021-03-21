/*
 * File name:
 * Fraction.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Represents a rational number in the form of a simplified fraction for use as the quota in OpenPartyListSystem and operations involving the quota
 */

package org.team19;

import java.util.Objects;

/**
 * Represents a rational number in the form of a simplified fraction
 * <p></p>
 * This class does not account for long overflows
 */
public class Fraction implements Comparable<Fraction> {
    
    /**
     * The numerator of this {@link Fraction}
     */
    protected long numerator;
    
    /**
     * The denominator of this {@link Fraction}
     */
    protected long denominator;
    
    /**
     * The truncation of the floating-point form of the {@link Fraction}, which is assigned upon the first usage of {@link #getWholePart()} and then
     * returned for any subsequent usage
     */
    protected Fraction wholePart;
    
    /**
     * This {@link Fraction} minus the truncation of its floating-point form, which is assigned upon the first usage of {@link #getFractionalPart()}
     * and then returned for any subsequent usage
     */
    protected Fraction fractionalPart;
    
    /**
     * The reciprocal of this {@link Fraction}, which is assigned upon the first usage of {@link #reciprocal()} and then returned for any
     * subsequent usage
     */
    protected Fraction reciprocal;
    
    /**
     * The result of calling {@link #toString()} on this {@link Fraction}, set on the first call to {@link #toString()}
     */
    protected String fractionStr;
    
    /**
     * Initializes a {@link Fraction} given a numerator and denominator in the form of a simplified fraction
     *
     * @param numerator   The numerator of the {@link Fraction}
     * @param denominator The denominator of the {@link Fraction}
     * @throws ArithmeticException Thrown if the denominator is zero
     */
    public Fraction(final long numerator, final long denominator) {
        if(denominator == 0) {
            throw new ArithmeticException(String.format("%d / %d is not a valid fraction as it has a denominator of zero", numerator, denominator));
        }
        this.numerator = numerator;
        this.denominator = denominator;
        simplify();
    }
    
    /**
     * The iterative form of the binary GCD algorithm, returning the gcd of the given two numbers if they are nonnegative
     * <p></p>
     * Credit to <a href="https://tinyurl.com/ysf3pmzu" target="_blank">uutils/coreutils</a> for the basis of this iterative binary GCD algorithm
     *
     * @param n1 The first of two numbers of which to find the gcd
     * @param n2 The second of two numbers of which to find the gcd
     * @return The gcd of the given two nonnegative numbers
     */
    protected static long gcd(long n1, long n2) {
        if(n1 == 0) {
            return n2;
        }
        else if(n2 == 0) {
            return n1;
        }
        
        //Get the number of trailing zeroes on the binary forms of each long
        final int n1TrailingZeroes = Long.numberOfTrailingZeros(n1);
        final int n2TrailingZeroes = Long.numberOfTrailingZeros(n2);
        
        //Divide each number by the largest power of 2 by which it is divisible
        n1 >>= n1TrailingZeroes;
        n2 >>= n2TrailingZeroes;
        
        //Store the exponent of the greatest power of 2 by which n1 and n2 are divisible
        final int greatestPowerOfTwoFactor = Math.min(n1TrailingZeroes, n2TrailingZeroes);
        
        //Implicit condition: while n2 is nonzero
        while(true) {
            //Switch n1 and n2 in the case where n1 > n2 before subtracting n1 from n2 (so that n1 and n2 are never negative)
            if(n1 > n2) {
                final long tmp = n1;
                n1 = n2;
                n2 = tmp;
            }
            n2 -= n1;
            
            //If n2 is zero, then n1 multiplied by the greatest power of 2 by which n1 and n2 are divisible is the gcd
            if(n2 == 0) {
                return n1 << greatestPowerOfTwoFactor;
            }
            
            //Otherwise, n1 is odd, so we can remove as many twos from n2 as possible as they will not be part of the gcd
            n2 >>= Long.numberOfTrailingZeros(n2);
        }
    }
    
    /**
     * Simplifies this {@link Fraction} by dividing the numerator and denominator by their gcf and also dividing them by -1 if the denominator is
     * negative
     */
    protected void simplify() {
        //If the numerator is 0, then we can make the fraction 0/1
        if(numerator == 0) {
            denominator = 1;
            return;
        }
        
        /*
         * Otherwise, we can divide both the numerator and denominator by their greatest common factor to simplify it
         *
         * We can also divide by -1 if the denominator is negative to convert fractions in the form a/-b and -a/-b to -a/b and a/b, respectively,
         * where a and b are positive integers
         */
        final int gcdMultiplier = denominator < 0 ? -1 : 1;
        final long signedNumDenomGcd = gcdMultiplier * gcd(Math.abs(numerator), Math.abs(denominator));
        numerator /= signedNumDenomGcd;
        denominator /= signedNumDenomGcd;
    }
    
    /**
     * Returns the numerator of this {@link Fraction}
     *
     * @return The numerator of this {@link Fraction}
     */
    public long getNumerator() {
        return numerator;
    }
    
    /**
     * Returns the denominator of this {@link Fraction}
     *
     * @return The denominator of this {@link Fraction}
     */
    public long getDenominator() {
        return denominator;
    }
    
    /**
     * Returns the truncation of the floating-point form of this {@link Fraction}
     *
     * @return The truncation of the floating-point form of this {@link Fraction}
     */
    public long getWholePart() {
        if(wholePart == null) {
            wholePart = new Fraction(numerator / denominator, 1);
        }
        return wholePart.getNumerator();
    }
    
    /**
     * Returns this {@link Fraction} minus the truncation of its floating-point form
     *
     * @return This {@link Fraction} the truncation of its floating-point form
     */
    public Fraction getFractionalPart() {
        if(fractionalPart == null) {
            if(wholePart == null) {
                getWholePart();
            }
            fractionalPart = subtract(wholePart);
        }
        return fractionalPart;
    }
    
    /**
     * Returns the value represented by the fraction as a double
     *
     * @return The value represented by the fraction as a double
     */
    public double getDoubleValue() {
        return (double) numerator / denominator;
    }
    
    /**
     * Returns the reciprocal of this {@link Fraction}
     *
     * @return The reciprocal of this {@link Fraction}
     * @throws ArithmeticException Thrown if the numerator is zero as the reciprocal would result in a division by zero
     */
    public Fraction reciprocal() {
        if(numerator == 0) {
            throw new ArithmeticException(String.format("The reciprocal of %d / %d would cause an illegal division by zero", numerator, denominator));
        }
        else if(reciprocal == null) {
            reciprocal = new Fraction(denominator, numerator);
        }
        return reciprocal;
    }
    
    /**
     * Returns the sum of this {@link Fraction} and the provided {@link Fraction}
     *
     * @param other The other fraction to sum with this fraction
     * @return The sum of this and the other fraction
     */
    public Fraction add(final Fraction other) {
        final long n1 = numerator, d1 = denominator;
        final long n2 = other.numerator, d2 = other.denominator;
        return new Fraction(n1 * d2 + n2 * d1, d1 * d2);
    }
    
    /**
     * Returns the difference of this {@link Fraction} and the provided {@link Fraction}
     *
     * @param other The other fraction to subtract from this fraction
     * @return The difference of this and the other fraction
     */
    public Fraction subtract(final Fraction other) {
        final long n1 = numerator, d1 = denominator;
        final long n2 = other.numerator, d2 = other.denominator;
        return new Fraction(n1 * d2 - n2 * d1, d1 * d2);
    }
    
    /**
     * Returns the product of this {@link Fraction} and the provided {@link Fraction}
     *
     * @param other The other fraction to multiply with this fraction
     * @return The product of this and the other fraction
     */
    public Fraction multiply(final Fraction other) {
        final long n1 = numerator, d1 = denominator;
        final long n2 = other.numerator, d2 = other.denominator;
        return new Fraction(n1 * n2, d1 * d2);
    }
    
    /**
     * Returns the quotient of this {@link Fraction} and the provided {@link Fraction}
     *
     * @param other The other fraction to act as the divisor in the quotient
     * @return The quotient of this and the other fraction
     */
    public Fraction divide(final Fraction other) {
        final long n1 = numerator, d1 = denominator;
        final long n2 = other.numerator, d2 = other.denominator;
        return new Fraction(n1 * d2, d1 * n2);
    }
    
    /**
     * Returns the result of comparing two {@link Fraction}s numerically
     *
     * @param other The other fraction to compare to this fraction
     * @return 0 if this is equal to other, -1 if this is less than other, and 1 if this is greater than other
     */
    public int compareTo(final Fraction other) {
        final long n1 = numerator, d1 = denominator;
        final long n2 = other.numerator, d2 = other.denominator;
        return Long.compare(n1 * d2, n2 * d1);
    }
    
    /**
     * Returns the string form of this {@link Fraction}
     *
     * @return the string form of this {@link Fraction} in the format "[numerator] / [denominator]", replacing [numerator] and [denominator]
     * accordingly
     */
    @Override
    public String toString() {
        if(fractionStr == null) {
            fractionStr = String.format("%d / %d", numerator, denominator);
        }
        return fractionStr;
    }
    
    /**
     * Returns true if the other object is a {@link Fraction} and has the same numerator and denominator
     *
     * @param other The object to compare to this {@link Fraction}
     * @return True if the other object is a {@link Fraction} and has the same numerator and denominator
     */
    @Override
    public boolean equals(final Object other) {
        if(this == other) {
            return true;
        }
        if(!(other instanceof Fraction)) {
            return false;
        }
        final Fraction fraction = (Fraction) other;
        return numerator == fraction.numerator && denominator == fraction.denominator;
    }
    
    /**
     * Returns the hashcode for this {@link Fraction}
     *
     * @return The hashcode for this {@link Fraction}
     */
    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }
}
