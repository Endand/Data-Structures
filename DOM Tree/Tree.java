package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
public void build() {
	Stack<TagNode> tagstack = new Stack<TagNode>();
	String html= sc.nextLine();
	root=new TagNode(html.substring(1,html.length()-1), null, null);
	tagstack.push(root); //to have an item in the stack to peek
	boolean startTag=false;
	while(sc.hasNextLine()) {
		startTag=false;
		String next= sc.nextLine();
		if(next.charAt(0)=='<') {
			if(next.charAt(1)=='/') {
				tagstack.pop();
				continue;
			}
			else {
				next=next.substring(1, next.length()-1); //get brackets off
				startTag=true;
			}
		} // if it's a tag
		TagNode element= new TagNode(next, null, null);
		if(tagstack.peek().firstChild==null) {
			tagstack.peek().firstChild = element;
		}
		else { //if there is firstChild
			TagNode fcCopy = tagstack.peek().firstChild;
			while(fcCopy.sibling != null) {
				fcCopy = fcCopy.sibling;
			}
			fcCopy.sibling = element;
		}
		if(startTag) {
			tagstack.push(element);
		}
	}
	
}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		if(oldTag!=null || newTag!=null) {
			replacetag (oldTag, newTag, root);}
			else return;
			
	}
	private void replacetag(String oldTag, String newTag, TagNode root){
		if(root==null) {
			return;
		}
		if(root.tag.equals(oldTag)) {
			root.tag=newTag;
		}
		replacetag( oldTag,  newTag, root.firstChild);
		replacetag( oldTag,  newTag, root.sibling);
		
		return;
		
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		boldRow(root, row);
	}
	private void boldRow(TagNode root, int row){
		if(row<1) {return;}
		if(root == null)
            return;
        if(root.tag.equals("table")){
            TagNode fc= root.firstChild;
           for(int i=1;i!=row;i++) {
              fc = fc.sibling;
            }
            TagNode fam;
            TagNode bold;
            for(fam = fc.firstChild; fam != null; fam = fam.sibling){
                bold = new TagNode("b",fam.firstChild,null);
                fam.firstChild = bold;
            }
        }
        boldRow(root.sibling, row);
        boldRow(root.firstChild, row);
        
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		if(tag.equals("p")||tag.equals("em")||tag.equals("b")) {
			removepemb(root,tag);
		}
		if(tag.equals("ol")||tag.equals("ul")) {
			removeList(root, tag);
		}
	}
	private void removepemb(TagNode root, String tag) {
		if(root==null) {
			return;
		}
		if(root.tag.equals(tag)) {
			TagNode temp= root.sibling; //save sibling
			root.tag=root.firstChild.tag; //transfer data
			root.sibling= root.firstChild.sibling; //change pointer
			root.firstChild=root.firstChild.firstChild;
			TagNode ptr= root.sibling;
			if(ptr!=null) {
			while(ptr.sibling!=null) {
				ptr=ptr.sibling;
			}
			ptr.sibling=temp;
			}
			else {
				root.sibling=temp;
			}
		}
		removepemb(root.firstChild, tag);
		removepemb(root.sibling, tag);
	}
private void removeList(TagNode root, String tag) {
	if(root==null) {
		return;
	}
	if(root.tag.equals(tag)&&root.firstChild != null) {
		TagNode temp= root.sibling; //save sibling
		TagNode liPtr = null;
		for(liPtr = root.firstChild; liPtr!= null; liPtr = liPtr.sibling){ //change all li to p
			liPtr.tag = "p"; 
		}
		root.tag=root.firstChild.tag; //transfer data
		root.sibling= root.firstChild.sibling; //change pointer
		root.firstChild=root.firstChild.firstChild;
		TagNode ptr= root.sibling;
		if(ptr!=null) {
		while(ptr.sibling!=null) {
			ptr=ptr.sibling;
		}
		ptr.sibling=temp;
		}
		else {
			root.sibling=temp;
		}
	}
	removeList(root.firstChild, tag);
	removeList(root.sibling, tag);
}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		if(tag.equals("em") || tag.equals("b")) {
			addTag( root,  word.toLowerCase(), tag);}
	}
	private void addTag(TagNode root, String word, String tag) {
		if(root == null){
			return; 
		}
	
		addTag(root.firstChild, word, tag);
		addTag(root.sibling, word, tag);
		boolean isTarget=false;
		if(root.firstChild == null){ //if it's a text node
			
			if(root.tag.toLowerCase().contains(word)) {
				String before="";
				String after="";
				StringTokenizer str = new StringTokenizer(root.tag, " ", true);
				while(str.hasMoreTokens()) {
				isTarget=false;	
				String token= str.nextToken();
				if(!str.hasMoreTokens()) {//if it's last token
					if(token.toLowerCase().contains(word.toLowerCase())){
						if(token.toLowerCase().charAt(0)==word.toLowerCase().charAt(0)) {//if first char matches to ensure it starts with word
							String temp= token.toLowerCase().replace(word.toLowerCase(), "");
							if(temp.equals("!") ||temp.equals("?")||temp.equals(".")||temp.equals(":")||temp.equals(";")||temp.equals("")) {
								isTarget=true;
							}
						}
					}
				}
				if(token.equalsIgnoreCase(word)) {isTarget=true;}
				
				if(!isTarget) {
					before+=token;
					}
				
				if(isTarget) { //time to add the tag
				root.tag=before;
				while(str.hasMoreTokens()) {
					after+=str.nextToken();
				}
				
				TagNode tokenNode = new TagNode(token, null, null); //node of the token(target) to be put as the firstChild of TagNode adding
				TagNode afterNode= new TagNode(after, null,null); //
				if(after.equals("")) {
					afterNode=null;
				}
				TagNode adding= new TagNode(tag, tokenNode, null);
				if(root.tag.equals("")||root.tag.equals(" ")) {
					root.tag=tag;
					root.firstChild=tokenNode;
					root.sibling=afterNode;
				}
				else {
				root.sibling=adding;
				adding.sibling=afterNode;
				}
				
				addTag(afterNode, word, tag);
				}
				}//while
			}
		}
		
	
	}
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}