
package ndproofs.logic;

    /**
     * Carries static methods for analysing logic.
     */
public class LogicAnalyser {
    
    
     public static boolean isChar(char checkChar) {
	if (checkChar == 'v' || checkChar == 'V')
		return false;

        if ((checkChar >= 'a') && (checkChar <= 'z'))
            return true;

        if ((checkChar >= 'A') && (checkChar <= 'Z'))
            return true;

        return false;
    }

    public static boolean isLogicSymbol(char checkChar) {

        if ((checkChar == '(') || (checkChar == ')'))
            return true;
        for (int i=1; i<5; i++)
            if (checkChar == Op.opChar[i])
                return true;

        return isChar(checkChar);
    }

    
    /**
     * @param stateString string to be checked
     * @return true iff statement is a valid logical statement
     */
    public static boolean isLogic(String stateString) {
        stateString = removeWhitespace(stateString);
        if (stateString.length() == 0)
            return false;
        
        // A. Reject wrong statements here.

        // Rules (Fundamental):
        // Accepted symbols: ( ) ^ v > ~
        // And all characters.
        for (int i=0; i<stateString.length(); i++) {
            if (!isLogicSymbol(stateString.charAt(i)))
                return false; // unacceptable symbol detected
        }

        // Rules (Precedence):
        // Line must start with (, A or ~
        // Precede (: Any operators
        // Precede ): No operator other than )
        // Precede A: A or operator other than )
        // Precede ~: Any operator other than )
        // Precede v ^ >: ) or A

        {
            char prevChar = '@'; // @ Character used to represent start of line.

            for (int i=0; i<stateString.length(); i++) {
                char curChar = stateString.charAt(i);

                // Line must start with (, A or ~
                // Precede (: Any operators
		if (curChar == '(') {
                        if (isChar(prevChar)) return false;
		}

                // Precede ): No operator other than )
		if (curChar == ')') {
                        if (prevChar == '@') return false;
                        if (prevChar == '(') return false;
                        if (prevChar == Op.opChar[Op.CON]) return false;
                        if (prevChar == Op.opChar[Op.DIS]) return false;
                        if (prevChar == Op.opChar[Op.IMP]) return false;
                        if (prevChar == Op.opChar[Op.NOT]) return false;
		}

                // Precede ~: Any operator other than )
		if (curChar == Op.opChar[Op.NOT]) {
                        if (prevChar == ')') return false;
                        if (isChar(prevChar)) return false;
                }

                // Precede v ^ >: ) or A
                if (curChar == Op.opChar[Op.CON] || curChar == Op.opChar[Op.DIS]
				|| curChar == Op.opChar[Op.IMP]) {
                    if (prevChar == '@') return false;
                    if (prevChar == '(') return false;
                    if (prevChar == Op.opChar[Op.CON]) return false;
                    if (prevChar == Op.opChar[Op.DIS]) return false;
                    if (prevChar == Op.opChar[Op.IMP]) return false;
                    if (prevChar == Op.opChar[Op.NOT]) return false;
                }

                // Precede A: A or operator other than )
                if (isChar(curChar)) {
                    if (prevChar == ')') return false;
                }

                prevChar = curChar;
            }
            
        // Line must end with ) or A
	// now prevChar is the last character of the string.
        	if (prevChar == Op.opChar[Op.CON]) return false;
        	if (prevChar == Op.opChar[Op.DIS]) return false;
        	if (prevChar == Op.opChar[Op.IMP]) return false;
        	if (prevChar == Op.opChar[Op.NOT]) return false;
        	if (prevChar == '(') return false;
	}

        // Rules (Logical):
        // 1. Every ( must correspond to a )
        // 2. There cannot be two > symbols on the same bracket level.
        // 3. There cannot be both v or ^ on the same bracket level.

        //1.2.3.
        {
            boolean[] hasIMP = new boolean[100];
            boolean[] hasCONDIS = new boolean[100];

            int bracketLevel = 0;
            for (int i=0; i<stateString.length(); i++) {
                char curChar = stateString.charAt(i);

                if (curChar == '(')
                    bracketLevel++;

                else if (curChar == ')') {
                    // Exit bracket level. Clear counter for hasIMP and hasCONDIS
                    hasIMP[bracketLevel] = false;
                    hasCONDIS[bracketLevel] = false;
                    bracketLevel--;
                }

                else if (curChar == '>') {
                    if (hasIMP[bracketLevel])
                        return false; // Error. More than one > in bracket Level.
                    hasIMP[bracketLevel] = true;
                }

                else if (curChar == 'v' || curChar == '^') {
                    if (hasCONDIS[bracketLevel])
                        return false; // Error. More than one v/^ in bracket Level.
                    hasCONDIS[bracketLevel] = true;
                }

                if (bracketLevel < 0)
                    return false; // bracketLevel should never go below zero.
            }
            if (bracketLevel != 0)
                return false; // brackets not balanced.
        }
        return true;
    }

    /**
     * Converts a string to a Logic object.
     * NOTE: Assumes that the stateString is a valid Logical statement.
     * Ensure that you check isLogic(stateString) before you use this.
     * 
     * @param stateString the String to be converted.
     * @return a Logic object converted from the string.
     */
    public static Logic readStatement(String stateString) {
        stateString = removeWhitespace(stateString);
        
        // textConsole.println(stateString); // DEBUGGING

        // B. Assuming statement is correct

        // Find the logic statement with the lowest priority.
        // Then split it into two substrings.

        // Creates a logical statement based on the string.

        // Operators: ( ) ^ v > ~
        // If anything is unreadable, it will taken as a variable name...


        // WE LOOK FOR THE LOWEST PRIORITY OPERATOR.
        // Lowest to highest:  1) >  2) ^,v  3) ~

        // WE IGNORE EVERYTHING WITHIN BRACKETS UNLESS EVERYTHING IS WITHIN BRACKETS.

        // ( adds one bracket level,  ) removes one bracket level. Bracket level 0 files are read.
        int bracketLevel = 0;

        // CASE 1:
        // Search for > symbol first.
        for (int i=0; i<stateString.length(); i++) {

            // Only search for chars at the highest bracket level
            if (stateString.charAt(i) == '(')
                bracketLevel++;
            else if (stateString.charAt(i) == ')')
                bracketLevel--;

            if ((bracketLevel == 0) && (stateString.charAt(i) == Op.opChar[Op.IMP])) {
                // Break up into two logical statements.
                return new Logic(Op.IMP, readStatement(stateString.substring(0,i)),
                        readStatement(stateString.substring(i+1,stateString.length())));
            }

        }
        
	// CASE  2:
        // Search for v or ^ symbol next.
        for (int i=0; i<stateString.length(); i++) {

            // Only search for chars at the highest bracket level
            if (stateString.charAt(i) == '(')
                bracketLevel++;
            else if (stateString.charAt(i) == ')')
                bracketLevel--;

            else {
                if ((bracketLevel == 0) && (stateString.charAt(i) == Op.opChar[Op.CON])) {
                    // Break up into two logical statements.
                    return new Logic(Op.CON, readStatement(stateString.substring(0,i)),
                            readStatement(stateString.substring(i+1,stateString.length())));
                }

                if ((bracketLevel == 0) && (stateString.charAt(i) == Op.opChar[Op.DIS])) {
                    // Break up into two logical statements.
                    return new Logic(Op.DIS, readStatement(stateString.substring(0,i)),
                            readStatement(stateString.substring(i+1,stateString.length())));
                }
            }

        }

        // CASE 3:
        //If we have reached this point, it means there are no >,v,^ characters at the highest bracket level.
        // The only possibility left is a statement of the form "~(statements)" or "variable"

        if (stateString.charAt(0) == Op.opChar[Op.NOT]) {
            return new Logic(Op.NOT, readStatement(stateString.substring(1,stateString.length())));
        }


        // CASE 4:
        // Next possibility. Everything within brackets.
        if ((stateString.charAt(0) == '(') && (stateString.charAt(stateString.length()-1) == ')'))
            return readStatement(stateString.substring(1,stateString.length()-1));


        // CASE 5:
        // Final possibility. No operators, nothing surrounded by brackets...
        // The rest must be a variable name.

        return new Logic(stateString);



    } // Read Statement - END
    
    
    
    public static String removeWhitespace(String input) {
        return input.replaceAll(" ", "");
    }
    
    public static String trimBrackets(String input) {
        if (input == null)
            return null;
        
        StringBuilder strBuild = new StringBuilder(input);
        
        boolean keepLooping = true;
        while(keepLooping) {
            if (strBuild.length() == 0)
                return "";
            
            keepLooping = false;
            
            while (strBuild.charAt(0) == ' ') {
                strBuild.deleteCharAt(0);
                keepLooping = true;
            }
            while (strBuild.charAt(strBuild.length()-1) == ' ') {
                strBuild.deleteCharAt(strBuild.length()-1);
                keepLooping = true;
            }
            while (encasedInBrackets(strBuild)) {
                strBuild.deleteCharAt(0);
                strBuild.deleteCharAt(strBuild.length()-1);
                keepLooping = true;
            }
        }
        return strBuild.toString();
    }

    /**
     * @param strBuild the expression
     * @return 
     * Returns true iff the whole expression is encased in brackets.
     * The brackets must be the first and last characters of the string.
     */
    public static boolean encasedInBrackets(StringBuilder strBuild) {
        if (strBuild.charAt(0) != '(' || strBuild.charAt(strBuild.length()-1) != ')')
            return false;
        
        int itr = 1;
        int bracketLevel = 1;
        
        for (int i=1; i<strBuild.length()-1; i++) {
            // Check whether bracketLevel goes back to 0 at any point.
            // If it never goes down to 0, that means the expression is encased in brackets.
            // If it goes down to 0, it might be something like this (AvB)^(CvD).
            if (strBuild.charAt(i) == '(')
                bracketLevel++;
            else if (strBuild.charAt(i) == ')') {
                bracketLevel--;
                if (bracketLevel <= 0)
                    return false;
            }
        }
        
        return true;
    }
}
