import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

public class PasswordCrack {
    static Random rand;
    public static void main(String[] args) throws IOException {
        rand = new Random();
        
        PreCompute.generateRainbowTable();
        
        if(Desktop.isDesktopSupported()) {
            Desktop.getDesktop().edit(new File("debug.txt"));
        }
        
        Scanner scan = new Scanner(System.in);
        String in = scan.nextLine();
        System.out.println(enterPassword(in));
        scan.close();
    }
    
    public static String enterPassword(String input) throws IOException {
        
        Scanner fileScanner = new Scanner(new File("text.txt"));
        
        String ans = search(PreCompute.hash(input), fileScanner);
        
        return ans;
    }
    public static String search(String inputHash, Scanner scan) {
        int rounds = 0;
        int count = 0;
        int chainLength = 50;
        int start = 48; //chainLength-2
        int numChains = 50;
        PriorityQueue queue = new PriorityQueue();
        PriorityQueue catchAll = new PriorityQueue();
        queue.add(start);
        
        //beginning of chain
        String plaintext = scan.nextLine();
        
        //ending hash of chain
        String endChainHash = scan.nextLine();
        
        String tempInputHash = inputHash;
        
        boolean falseAlarm = false;
      //if you've rehashed x times and found nothing, it's just not in the table - end result will be properly modified
        while(count <= chainLength-1) {;

            //if the hashes are equal, traverse the chain
            if(endChainHash.equals(tempInputHash) && !falseAlarm) {
                String tempPlainText = plaintext;
                String currHash = PreCompute.hash(tempPlainText);
                
                //for the length of a chain
                for(int z = 1; z <= chainLength; z++) {

                    //if you found the password, return it!
                    if(currHash.equals(inputHash)) {
                       return tempPlainText; 
                    } else {
                        //if you didn't find it, bump up the hashcode and plaintext along the chain
                            tempPlainText = PreCompute.reduction(currHash, z-1);
                            currHash = PreCompute.hash(tempPlainText);
                    }
                }
            } else {
                //go down the text document if unsuccessful
                if(scan.hasNext()) {
                    scan.nextLine();
                }
               if(scan.hasNext()) {
                   plaintext = scan.nextLine();
                   
               }
                if(scan.hasNext()) {
                    endChainHash = scan.nextLine();
                    
                }
                rounds++;
            }
            //if n chains have been checked
            if(rounds == numChains) {
                //scan.close();
                try {
                    scan = new Scanner(new File("text.txt"));
                } catch (FileNotFoundException e) {
                    System.out.println("File wasn't found later on");
                }
                
                rounds = 0;
                count++;
                
                plaintext = scan.nextLine();
                endChainHash = scan.nextLine();
                
                //reduce and rehash the current variation of the input hash
                tempInputHash = inputHash;
                
                for(int p = 0; p < count; p++) {

                    int x = (int) queue.remove();
                    
                    catchAll.add(x);
                    String temp = PreCompute.reduction(tempInputHash, x);
                    tempInputHash = PreCompute.hash(temp);
                    
                }
                
                    start--;
                    queue.add(start);
                    
                while(!catchAll.isEmpty()) {
                    queue.add(catchAll.remove());
                }
            }
        }
        return "not found";
    }
}
