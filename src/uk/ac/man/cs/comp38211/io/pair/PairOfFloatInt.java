WeChat: cstutorcs
QQ: 749389476
Email: tutorcs@163.com
/*
 * Cloud9: A Hadoop toolkit for working with big data Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package uk.ac.man.cs.comp38211.io.pair;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * WritableComparable representing a pair consisting of a float and an int. The
 * elements in the pair are referred to as the left element (the float) and
 * right element (the int). The natural sort order is: first by the left
 * element, and then by the right element.
 * 
 * @author Jimmy Lin
 */
public class PairOfFloatInt implements WritableComparable<PairOfFloatInt>
{
    private float leftElement;
    private int rightElement;

    /**
     * Creates a pair.
     */
    public PairOfFloatInt()
    {
    }

    /**
     * Creates a pair.
     * 
     * @param left
     *            the left element
     * @param right
     *            the right element
     */
    public PairOfFloatInt(float left, int right)
    {
        set(left, right);
    }

    /**
     * Deserializes this pair.
     * 
     * @param in
     *            source for raw byte representation
     */
    public void readFields(DataInput in) throws IOException
    {
        leftElement = in.readFloat();
        rightElement = in.readInt();
    }

    /**
     * Serializes this pair.
     * 
     * @param out
     *            where to write the raw byte representation
     */
    public void write(DataOutput out) throws IOException
    {
        out.writeFloat(leftElement);
        out.writeInt(rightElement);
    }

    /**
     * Returns the left element.
     * 
     * @return the left element
     */
    public float getLeftElement()
    {
        return leftElement;
    }

    /**
     * Returns the right element.
     * 
     * @return the right element
     */
    public int getRightElement()
    {
        return rightElement;
    }

    /**
     * Returns the key (left element).
     * 
     * @return the key
     */
    public float getKey()
    {
        return leftElement;
    }

    /**
     * Returns the value (right element).
     * 
     * @return the value
     */
    public int getValue()
    {
        return rightElement;
    }

    /**
     * Sets the right and left elements of this pair.
     * 
     * @param left
     *            the left element
     * @param right
     *            the right element
     */
    public void set(float left, int right)
    {
        leftElement = left;
        rightElement = right;
    }

    /**
     * Checks two pairs for equality.
     * 
     * @param obj
     *            object for comparison
     * @return <code>true</code> if <code>obj</code> is equal to this object,
     *         <code>false</code> otherwise
     */
    public boolean equals(Object obj)
    {
        PairOfFloatInt pair = (PairOfFloatInt) obj;
        return leftElement == pair.getLeftElement()
                && rightElement == pair.getRightElement();
    }

    /**
     * Defines a natural sort order for pairs. Pairs are sorted first by the
     * left element, and then by the right element.
     * 
     * @return a value less than zero, a value greater than zero, or zero if
     *         this pair should be sorted before, sorted after, or is equal to
     *         <code>obj</code>.
     */
    public int compareTo(PairOfFloatInt pair)
    {
        float pl = pair.getLeftElement();
        int pr = pair.getRightElement();

        if (leftElement == pl)
        {
            if (rightElement < pr) return -1;
            if (rightElement > pr) return 1;
            return 0;
        }

        if (leftElement < pl) return -1;

        return 1;
    }

    /**
     * Returns a hash code value for the pair.
     * 
     * @return hash code for the pair
     */
    public int hashCode()
    {
        return (int) leftElement + rightElement;
    }

    /**
     * Generates human-readable String representation of this pair.
     * 
     * @return human-readable String representation of this pair
     */
    public String toString()
    {
        return "(" + leftElement + ", " + rightElement + ")";
    }

    /**
     * Clones this object.
     * 
     * @return clone of this object
     */
    public PairOfFloatInt clone()
    {
        return new PairOfFloatInt(this.leftElement, this.rightElement);
    }

    /** Comparator optimized for <code>PairOfFloatInt</code>. */
    public static class Comparator extends WritableComparator
    {

        /**
         * Creates a new Comparator optimized for <code>PairOfFloatInt</code>.
         */
        public Comparator()
        {
            super(PairOfFloatInt.class);
        }

        /**
         * Optimization hook.
         */
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
        {
            float thisLeftValue = readFloat(b1, s1);
            float thatLeftValue = readFloat(b2, s2);

            if (thisLeftValue == thatLeftValue)
            {
                int thisRightValue = readInt(b1, s1 + 4);
                int thatRightValue = readInt(b2, s2 + 4);

                return (thisRightValue < thatRightValue ? -1
                        : (thisRightValue == thatRightValue ? 0 : 1));
            }

            return (thisLeftValue < thatLeftValue ? -1
                    : (thisLeftValue == thatLeftValue ? 0 : 1));
        }
    }

    static
    { // register this comparator
        WritableComparator.define(PairOfFloatInt.class, new Comparator());
    }
}
