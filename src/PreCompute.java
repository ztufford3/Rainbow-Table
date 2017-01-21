import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;


public class PreCompute {
    static ArrayList<int[]> exclusivityMatrix;
    public static void generateRainbowTable() throws IOException {
        BufferedWriter out = null;
        BufferedWriter ov = null;
       try {
           
            ov = new BufferedWriter(new FileWriter("debug.txt"));
            out = new BufferedWriter(new FileWriter("text.txt"));
            
       } catch(Exception e) {
           
           System.out.println("writer problems");
           
       }
       
       int numChains = 50;
       int chainLength = 50;
       
       exclusivityMatrix = generateExclusivityMatrix(chainLength-1);
       
       //number of chains
       for(int i = 0; i < numChains; i++) {
           String current = randomString();
           out.write(current);
           out.newLine();
           ov.write(current);
           ov.newLine();
           
           //length of chain
           for(int j = 1; j < chainLength; j++) {
               String hashc = hash(current);
               ov.write(hashc);
               ov.newLine();
               current = reduction(hashc, j-1);
               ov.write(current);
               ov.newLine();
           }

           out.write(hash(current));
           out.newLine();
           out.newLine();
           ov.write(hash(current));
           ov.newLine();
           ov.newLine();
           ov.newLine();
       }

       ov.flush();
       ov.close();
       out.flush();
       out.close();

    }
    
    public static String hash(String message) {
        String out = "";
        
        try {
            
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            
            byte[] arr = mDigest.digest(message.getBytes("UTF-8"));
            
            StringBuilder temp = new StringBuilder(2*arr.length);
            
            for(byte b : arr) {
                
                temp.append(String.format("%02x", b&0xff));
                
            }
            
            out = temp.toString();
            
        } catch (UnsupportedEncodingException ex) {
            
            System.out.println("Hash function isn't working. UnsupportedEncodingException.");
            
        } catch (NoSuchAlgorithmException ex) {
            
            System.out.println("Hash function isn't working. NoSuchAlgorithmException.");
            
        }
        
        return out;
    }
    
    //produces a different reduction function for each link (consistent across columns)
    public static String reduction(String message, int numLink) {

        return shiftCharacters(message.substring(0,5) , exclusivityMatrix.get(numLink));
    }
    
    public static ArrayList<int[]> generateExclusivityMatrix(int length) {

        ArrayList<int[]> out = new ArrayList<int[]>();
        Random rand = new Random();
        
        for(int i = 0; i < length; i++) {
            int[] temp = new int[5];
            
            for(int k = 0; k < 5; k++) {
                temp[k] = rand.nextInt(10);
            }
            
            if(out.contains(temp)) {
                i--;
            } else {
                out.add(temp);
            }
        }
        
        return out;
    }
    
    public static String randomString() {
        Random rand = new Random();
        String output = "";
        for(int i = 0; i < 5; i++) {
            int chance = rand.nextInt(10);
            char plus;
            if(chance < 5) {
                plus = getRandomowerCaseLetter();
            } else {
                plus = getRandomDigitCharacter();
            }
            output += Character.toString(plus);
        }
        return output;
    }
    
    public static String shiftCharacters(String message, int[] matrix) {
        char[] temp = new char[message.length()];
        for(int i = 0; i < message.length(); i++) {
            temp[i] = message.charAt(i);
        }
        
        for(int k = 0; k < temp.length; k++) {
            if(Character.isLetter(temp[k])) {
                if((int) temp[k] + matrix[k] >= 123) {
                    temp[k] = (char) (((int) (temp[k] + matrix[k]))%123 + 97);
                } else {
                    temp[k] = (char) (((int) (temp[k] + matrix[k])));
                }
            } else {
                if(temp[k] + matrix[k] >= 58) {
                    temp[k] = (char) (((int) (temp[k] + matrix[k]))%58 + 48);
                } else {
                    temp[k] = (char) (((int) (temp[k] + matrix[k])));
                }
            }
        }
        
        String out = "";
        
        for(int m = 0; m < temp.length; m++) {
            out += temp[m];
        }
        
        return out;
    }
    
    public static char getRandomCharacter(char ch1, char ch2) {
        char char1 =  (char) (ch1 + Math.random() * (ch2 - ch1 + 1));
        
        return char1;
    }
    
    public static char getRandomowerCaseLetter() {
        return getRandomCharacter('a', 'z');
    }
    
    public static char getRandomDigitCharacter() {
        return getRandomCharacter('0', '9');
    }
    
    public static boolean identical(int[] one, int[] two) {
        for(int i = 0; i < one.length; i++) {
            if(one[i] != two[i])
                return false;
        }
        return true;
    }
    
    public static boolean uniqueTest() {
        boolean weFailed = false;
        
        for(int i = 0; i < exclusivityMatrix.size(); i++) {
            int[] x = exclusivityMatrix.remove(i);
            for(int m = 0; m < exclusivityMatrix.size(); m++) {
                if(identical(x, exclusivityMatrix.get(m))) {
                    weFailed = true;
                }
            }
        }
        
        if(weFailed) {
            return false; //not unique
        }
        return true;
    }

    public static void printArrayList() {
        for(int i = 0; i < exclusivityMatrix.size(); i++) {
            int[] temp = exclusivityMatrix.get(i);
            for(int k = 0; k < temp.length; k++) {
                System.out.print(temp[k] + " ");
            }
            System.out.println();
        }
    }
    
}
