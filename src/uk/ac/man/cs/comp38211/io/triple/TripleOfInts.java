WeChat: cstutorcs
QQ: 749389476
Email: tutorcs@163.com
/*
 * Cloud9: A MapReduce Library for Hadoop Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package uk.ac.man.cs.comp38211.io.triple;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * <p>
 * WritableComparable representing a pair of integers. The elements in the pair
 * are referred to as the left and right elements. The natural sort order is:
 * first by the left element, and then by the right element.
 * </p>
 * 
 * @author Ke Zhai
 */
public class TripleOfInts implements WritableComparable<TripleOfInts>
{
    private int leftElement, middleElement, rightElement;

    /**
     * Creates a <code>TripleOnInts</code> object.
     */
    public TripleOfInts()
    {
    }

    /**
     * Creates a <code>TripleOnInts</code> object.
     * 
     * @param left
     *            the left element
     * @param middle
     *            the middle element
     * @param right
     *            the right element
     */
    public TripleOfInts(int left, int middle, int right)
    {
        set(left, middle, right);
    }

    /**
     * Deserializes this object.
     * 
     * @param in
     *            source for raw byte representation
     */
    public void readFields(DataInput in) throws IOException
    {
        leftElement = in.readInt();
        middleElement = in.readInt();
        rightElement = in.readInt();
    }

    /**
     * Serializes this object.
     * 
     * @param out
     *            where to write the raw byte representation
     */
    public void write(DataOutput out) throws IOException
    {
        out.writeInt(leftElement);
        out.writeInt(middleElement);
        out.writeInt(rightElement);
    }

    /**
     * Returns the left element.
     * 
     * @return the left element
     */
    public int getLeftElement()
    {
        return leftElement;
    }

    /**
     * Returns the middle element.
     * 
     * @return the middle element
     */
    public int getMiddleElement()
    {
        return middleElement;
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
     * Sets the elements of this triple.
     * 
     * @param left
     *            the left element
     * @param middle
     *            the middle element
     * @param right
     *            the right element
     */
    public void set(int left, int middle, int right)
    {
        leftElement = left;
        middleElement = middle;
        rightElement = right;
    }

    /**
     * Checks for equality.
     * 
     * @param obj
     *            object for comparison
     * @return <code>true</code> if <code>obj</code> is equal to this object,
     *         <code>false</code> otherwise
     */
    public boolean equals(Object obj)
    {
        TripleOfInts threeInts = (TripleOfInts) obj;
        return leftElement == threeInts.getLeftElement()
                && middleElement == threeInts.getMiddleElement()
                && rightElement == threeInts.getRightElement();
    }

    /**
     * Defines a natural sort order for pairs. Pairs are sorted first by the
     * left element, and then by the right element.
     * 
     * @return a value less than zero, a value greater than zero, or zero if
     *         this pair should be sorted before, sorted after, or is equal to
     *         <code>obj</code>.
     */
    public int compareTo(TripleOfInts obj)
    {
        TripleOfInts threeInts = (TripleOfInts) obj;

        int pl = threeInts.getLeftElement();
        int pm = threeInts.getMiddleElement();
        int pr = threeInts.getRightElement();
        if (leftElement < pl)
        {
            return -1;
        }
        else if (leftElement > pl)
        {
            return 1;
        }
        else
        {
            if (middleElement < pm)
            {
                return -1;
            }
            else if (middleElement > pm)
            {
                return 1;
            }
            else
            {
                if (rightElement < pr)
                {
                    return -1;
                }
                else if (rightElement > pr)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
        }
    }

    /**
     * Returns a hash code value for this ThreeInts instance.
     * 
     * @return hash code for this ThreeInts instance
     */
    public int hashCode()
    {
        return leftElement + middleElement + rightElement;
    }

    /**
     * Generates human-readable String representation of this pair.
     * 
     * @return human-readable String representation of this pair
     */
    public String toString()
    {
        return "(" + leftElement + ", " + middleElement + ", " + rightElement
                + ")";
    }

    /**
     * Clones this object.
     * 
     * @return clone of this object
     */
    public TripleOfInts clone()
    {
        return new TripleOfInts(this.leftElement, this.middleElement,
                this.rightElement);
    }

    /** Comparator optimized for <code>TripleOfInts</code>. */
    public static class Comparator extends WritableComparator
    {

        /**
         * Creates a new Comparator optimized for <code>TripleOfInts</code>.
         */
        public Comparator()
        {
            super(TripleOfInts.class);
        }

        /**
         * Optimization hook.
         */
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
        {
            int thisLeftValue = readInt(b1, s1);
            int thatLeftValue = readInt(b2, s2);

            if (thisLeftValue < thatLeftValue)
            {
                return -1;
            }
            else if (thisLeftValue > thatLeftValue)
            {
                return 1;
            }
            else
            {
                int thisMiddleValue = readInt(b1, s1 + 4);
                int thatMiddleValue = readInt(b2, s2 + 4);
                if (thisMiddleValue < thatMiddleValue)
                {
                    return -1;
                }
                else if (thisMiddleValue > thatMiddleValue)
                {
                    return 1;
                }
                else
                {
                    int thisRightValue = readInt(b1, s1 + 8);
                    int thatRightValue = readInt(b2, s2 + 8);
                    if (thisRightValue < thatRightValue)
                    {
                        return -1;
                    }
                    else if (thisRightValue > thatRightValue)
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            }
        }
    }

    static
    { // register this comparator
        WritableComparator.define(TripleOfInts.class, new Comparator());
    }
}
