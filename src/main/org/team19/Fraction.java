package org.team19;

import java.util.Objects;

/**
 * Represents a rational number in the form of a simplified fraction
 * <p></p>
 * This class does not account for long overflows
 */
public final class Fraction {
    
    /**
     * The numerator of this {@link Fraction}
     */
    private long numerator;
    
    /**
     * The denominator of this {@link Fraction}
     */
    private long denominator;
    
    /**
     * The floor of this {@link Fraction}, which is assigned upon the first usage of {@link #getWholePart()} and then returned for any subsequent
     * usage
     */
    private Long wholePart;
    
    /**
     * This {@link Fraction} minus its floor, which is assigned upon the first usage of {@link #getFractionalPart()} and then returned for any
     * subsequent usage
     */
    private Fraction fractionalPart;
    
    /**
     * The reciprocal of this {@link Fraction}, which is assigned upon the first usage of {@link #reciprocal()} and then returned for any
     * subsequent usage
     */
    private Fraction reciprocal;
    
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
     * @param n1Final The first of two numbers of which to find the gcd
     * @param n2Final The second of two numbers of which to find the gcd
     * @return The gcd of the given two nonnegative numbers
     */
    public static long gcd(final long n1Final, final long n2Final) {
        if(n1Final == 0) {
            return n2Final;
        }
        else if(n2Final == 0) {
            return n1Final;
        }
        
        long n1 = n1Final;
        long n2 = n2Final;
        
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
    private void simplify() {
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
        final long sigNumDenomGcd = gcdMultiplier * gcd(Math.abs(numerator), Math.abs(denominator));
        numerator /= sigNumDenomGcd;
        denominator /= sigNumDenomGcd;
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
     * Returns the floor of this {@link Fraction}
     *
     * @return The floor of this {@link Fraction}
     */
    public long getWholePart() {
        if(wholePart == null) {
            wholePart = numerator / denominator;
        }
        return wholePart;
    }
    
    /**
     * Returns this {@link Fraction} minus its floor
     *
     * @return This {@link Fraction} minus its floor
     */
    public Fraction getFractionalPart() {
        if(fractionalPart == null) {
            fractionalPart = subtract(new Fraction(getWholePart(), 1));
        }
        return fractionalPart;
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
     * Returns true if the other object is a {@link Fraction} and has the same numerator and denominator as this {@link Fraction}
     *
     * @param other The object to compare to this {@link Fraction}
     * @return True if the other object is a {@link Fraction} and has the same numerator and denominator as this {@link Fraction}
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
    
    /**
     * Returns the String form of this fraction
     *
     * @return the String form of this fraction in the format "[numerator] / [denominator]", replacing [numerator] and [denominator] accordingly
     */
    @Override
    public String toString() {
        return String.format("%d / %d", numerator, denominator);
    }
}
