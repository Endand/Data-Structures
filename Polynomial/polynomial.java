package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		Node ptr = poly1;
		Node subptr=poly2;
		Node last= new Node(0,0,null);
		Node tmp= last;
		
		 while(ptr!=null || subptr!=null) { 
			 if(ptr==null) {
				 last.next=subptr;
				 return tmp.next;
			 }
			 if(subptr==null) {
				 last.next=ptr;
				 return tmp.next;
			 }
				if(ptr.term.degree==subptr.term.degree) {
					if(ptr.term.coeff+subptr.term.coeff!=0.0) {
					Node n = new Node(ptr.term.coeff+subptr.term.coeff,ptr.term.degree,null);
					last.next=n;
					last=last.next;	}
					subptr=subptr.next;	
					ptr=ptr.next;
					
				}
				else if(ptr.term.degree>subptr.term.degree) {
					if(subptr.term.coeff!=0.0) {
					Node n = new Node(subptr.term.coeff, subptr.term.degree, null);
					last.next=n;}
					last=last.next;	
					subptr=subptr.next;	
					
				}
				else if(subptr.term.degree>ptr.term.degree) {
					if(ptr.term.coeff!=0.0) {
					Node n = new Node(ptr.term.coeff, ptr.term.degree, null);
					last.next=n;}
					last=last.next;	
					ptr=ptr.next;
			}
		
	}
		 return tmp.next;
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		if(poly1==null || poly2==null) {
			return null;
		}
		Node ptr = poly1;
		Node subptr=poly2;
		Node last= new Node(0,0,null);
		Node tmp= last;
		while(ptr!=null) {
			while(subptr!=null) {
				Node n= new Node(ptr.term.coeff*subptr.term.coeff,ptr.term.degree+subptr.term.degree,null);
				last.next=n;
				last=last.next;
				subptr=subptr.next;
			}
			subptr=poly2;
			ptr=ptr.next;
		}
	Node base=tmp.next;
	Node extra=base.next;
	base.next=null;
  while (extra != null) {
		Node n = new Node(extra.term.coeff, extra.term.degree, null);
		base=add(base,n);
		extra=extra.next;
  }
  return base;
}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		Node ptr=poly;
		float result=0;
		if(x==0) {
			return result;
		}
		while(ptr!=null) {
			result+= ptr.term.coeff* Math.pow(x, ptr.term.degree);
			ptr=ptr.next;
		}
		return result;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}