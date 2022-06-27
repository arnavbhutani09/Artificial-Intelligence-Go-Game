package aihw2;
import java.io.*;
import java.util.*;
public class my_player {
     static int[][] g_current_board;
     static int[][] g_previous_board;
     static int totaldepth;
     static int totalmoves;
     static int globalturn;
     static int currentstep;
   
     public static double eval(int[][] current_board, int moves){
        int myplayer = globalturn;
        int oppplayer = 3-globalturn;
        int mycorner = 0;
        int myside = 0;
        int myliberty = 0;
        int myplayerconnects = 0;
        int eyeconnects=0;
        int oppliberty=0;
        int  myplayerdeathcount=0;
        int oppplayerdeathcount=0;
        int mpe=0;
        int ope=0;
        int mpk=0;
        int opk=0;
        int mpr=0;
        int opr=0;
        int mycount=0;
        int oppcount = 0;
        double heuans=0;
         ArrayList<ArrayList<Integer>> neighbors = new ArrayList<ArrayList<Integer>>();
         for(int i=0;i<5;i++){
             for(int j=0;j<5;j++){
                
                if(current_board[i][j]==myplayer){
                    boolean ifeye8 = true;
                    mycount++;
                    if(checkcorner(current_board,i,j)){
                         mycorner++;
                    }
                    else if(checkside(current_board,i,j)){
                      myside++;
                    }
                    if(find_liberty(current_board,new ArrayList<Integer>(Arrays.asList(i,j)),globalturn).size()!=0){
                        myliberty++;
                    }
                    neighbors = eyecheck(current_board,i, j);
                    for(ArrayList<Integer> neighbor: neighbors){
                        if(myplayer!=current_board[neighbor.get(0)][neighbor.get(1)]){
                                ifeye8=false;
                                 break;
                        }
                    }
                    myplayerconnects = myplayerconnects + neighbors.size();
                    if(ifeye8){
                        eyeconnects++;
                    }
                }
                else if(current_board[i][j]==oppplayer){
                    oppcount++;
                    neighbors = eyecheck(current_board,i, j);
                    if(find_liberty(current_board,new ArrayList<Integer>(Arrays.asList(i,j)),oppplayer).size()!=0){
                        oppliberty++;
                    }

                }
               
                if(g_current_board[i][j]==myplayer){
                    if(deathchecker(current_board,i,j,myplayer)){
                        myplayerdeathcount++;
                    }
                }else if(g_current_board[i][j]==oppplayer){
                     if(deathchecker(current_board,i,j,oppplayer)){
                        oppplayerdeathcount++;
                    }
                }
                if(i>=1 && i<=3 && j>=1 && j<=3 && rhomcheck(current_board,i,j)){
                        if(current_board[i][j]!=0){
                            if(current_board[i+1][j]==myplayer){
                                mpk++;
                            }else if(current_board[i+1][j]==oppplayer){
                                opk++;
                            }
                        }else if(current_board[i][j]==0){
                            if(current_board[i+1][j]==myplayer){
                                mpr++;
                            }else if(current_board[i+1][j]==oppplayer){
                                opr++;
                            }
                        }
                }

                if(i>=1 && j>=1){
                    if(eboundary1(current_board,i,j,myplayer)){
                        mpe++;
                    }
                    if(eboundary1(current_board,i,j,oppplayer)){
                        ope++;
                    }
                }
                if(i>=1 && j<=3){
                     if(eboundary2(current_board,i,j,myplayer)){
                        mpe++;
                    }
                    if(eboundary2(current_board,i,j,oppplayer)){
                        ope++;
                    }
                }
                 if(i<=3 && j>=1){
                     if(eboundary3(current_board,i,j,myplayer)){
                        mpe++;
                    }
                    if(eboundary3(current_board,i,j,oppplayer)){
                        ope++;
                    }
                }
                if(i<=3 && j<=3){
                     if(eboundary4(current_board,i,j,myplayer)){
                        mpe++;
                    }
                    if(eboundary4(current_board,i,j,oppplayer)){
                        ope++;
                    }
                }
             }
         }
         if(myplayer != 2){
             oppcount += 2.5;
         }

         heuans = heuans + playercounterheu(current_board,moves, mycount, oppcount);
         heuans = heuans + edgycounterheu(current_board,moves,myside,mycorner);
         heuans = heuans + killyheu(current_board,moves,myplayerdeathcount,oppplayerdeathcount);
         heuans = heuans + (1.99* (myliberty - oppliberty));
         heuans = heuans + ((-4.99-10)*eyeconnects);
         heuans = heuans + (2+2.49)*(mpe-ope);
         heuans = heuans + ((-4.99-10)*(mpk-opk));
         heuans = heuans+ ((2+3.49)*(mpr-opr));
         heuans = heuans + (1.99*myplayerconnects);
         return heuans;

          
     }
     public static int killyheu(int[][] current_board,int moves,int myplayerdeathcount,int oppplayerdeathcount){
         if(globalturn!=2){
             return 4*(oppplayerdeathcount-myplayerdeathcount)*2*20;
         }
         return 2*(oppplayerdeathcount-myplayerdeathcount)*20;
     }
     public static double playercounterheu(int[][] current_board, int moves, double mycount,double oppcount){
            if(moves>19){
                return 2*(mycount-oppcount)*2*2;
            }
            return 2*(mycount-oppcount);
     }

     public static double edgycounterheu(int[][] current_board, int moves, int myside,int mycorner){
            mycorner = 2*mycorner*2;
            if(moves<10){
                return 2*(-mycorner-(double)myside)*100;
            }
            return (-mycorner-(double)myside);
     }
    
     public static boolean eboundary1(int[][] current_board,int i, int j, int chance){
                    if(current_board[i-1][j-1]==current_board[i][j]){
                        if(current_board[i][j]==chance){
                            return true;
                        }
                    }
            return false;
     }
      public static boolean eboundary2(int[][] current_board,int i, int j, int chance){
           if(current_board[i-1][j+1]==current_board[i][j]){
                        if(current_board[i][j]==chance){
                            return true;
                        }
                    }
            return false;
     }
      public static boolean eboundary3(int[][] current_board,int i, int j, int chance){
         if(current_board[i+1][j-1]==current_board[i][j]){
                        if(current_board[i][j]==chance){
                            return true;
                        }
                    }
            return false;
     }
      public static boolean eboundary4(int[][] current_board,int i, int j, int chance){
          if(current_board[i+1][j+1]==current_board[i][j]){
                        if(current_board[i][j]==chance){
                            return true;
                        }
                    }
            return false;
     }
     public static boolean deathchecker(int[][] current_board,int i, int j, int turn){
             if(current_board[i][j]!=turn){
                 return true;
             }
            return false;
     }

      public static boolean rhomcheck(int[][] current_board, int i, int j){
         if(current_board[i-1][j]==current_board[i+1][j]&&current_board[i][j-1]==current_board[i][j+1]&& current_board[i][j-1]== current_board[i-1][j]){
             return true;
         }
         return false;
     }
     public static ArrayList<ArrayList<Integer>> eyecheck(int[][] current_board,int i, int j){
         ArrayList<ArrayList<Integer>> neighbors = new ArrayList<ArrayList<Integer>>();
         if(i>0){
            neighbors.add(new ArrayList<Integer>(Arrays.asList(i-1,j)));
         }
         if(j>0){
            neighbors.add(new ArrayList<Integer>(Arrays.asList(i,j-1)));
         }
        if(i<4){
            neighbors.add(new ArrayList<Integer>(Arrays.asList(i+1,j)));
         }
        if(j<4){
            neighbors.add(new ArrayList<Integer>(Arrays.asList(i,j+1)));
         }
       if(i>=1 && j>=1){
            neighbors.add(new ArrayList<Integer>(Arrays.asList(i-1,j-1)));
       }
       if(i<=3 && j<=3){
            neighbors.add(new ArrayList<Integer>(Arrays.asList(i+1,j+1)));
       }
       if(i>=1 && j<=3){
            neighbors.add(new ArrayList<Integer>(Arrays.asList(i-1,j+1)));
       }
       if(i<=3 && j>=1){
            neighbors.add(new ArrayList<Integer>(Arrays.asList(i+1,j-1)));
       }
       return neighbors;
     }

      public static boolean checkcorner(int[][] current_board,int i, int j){
        if(i==0 && j==0)
            return true;
        if(i==0 && j==4)
            return true;
        if(i==4 && j==0)
            return true;
        if(i==4 && j==4)
            return true;
        return false;
    }

    public static boolean checkside(int[][] current_board,int i, int j){
        if(i==0)
            return true;
        if(j==0)
            return true;
        if(i==4)
            return true;
        if(j==4)
            return true;
        return false;
    }
     public static ArrayList<ArrayList<Integer>> findallmoves(int[][] current_board){
         ArrayList<ArrayList<Integer>> allmoves = new ArrayList<ArrayList<Integer>>();
         for(int i=0;i<5;i++) {
             for(int j=0;j<5;j++) {
                 ArrayList<Integer> toadd = new ArrayList<Integer>();
                 if(current_board[i][j]==0) {
                    toadd.add(i);
                    toadd.add(j);
                    allmoves.add(toadd);
                 }
             }
         }
         return allmoves;
     }
     public static ArrayList<ArrayList<Integer>> detect_neighbor(int[][] current_board, ArrayList<Integer> position, int turn){
         ArrayList<ArrayList<Integer>> neighbors = new ArrayList<ArrayList<Integer>>();
         int i = position.get(0);
         int j = position.get(1);
        ArrayList<Integer> toadd = new ArrayList<Integer>();
         if(i>0){
             toadd.add(i-1);
             toadd.add(j);
             neighbors.add(toadd);
         }
         toadd = new ArrayList<Integer>();
         if(i<4) {
             toadd.add(i+1);
             toadd.add(j);
             neighbors.add(toadd);
         }
         toadd = new ArrayList<Integer>();
         if(j>0) {
             toadd.add(i);
             toadd.add(j-1);
             neighbors.add(toadd);
         }
         toadd = new ArrayList<Integer>();
         if(j<4) {
             toadd.add(i);
             toadd.add(j+1);
             neighbors.add(toadd);
         }
         return neighbors;
     }
     public static ArrayList<ArrayList<Integer>> detect_neighbor_ally(int[][] current_board, ArrayList<Integer> position, int turn){
         ArrayList<Integer> adder;
         ArrayList<ArrayList<Integer>> neighbors = detect_neighbor(current_board, position, turn);
         ArrayList<ArrayList<Integer>> group_allies = new ArrayList<ArrayList<Integer>>();
         for(ArrayList<Integer> x : neighbors){
              if(current_board[x.get(0)][x.get(1)]==current_board[position.get(0)][position.get(1)]){
                 adder = new ArrayList<Integer>(Arrays.asList(x.get(0),x.get(1)));
                 group_allies.add(adder);
             }
         }
         return group_allies;
     }
     public static  ArrayList<ArrayList<Integer>> ally_dfs(int[][] current_board, ArrayList<Integer> position, int turn){
         Stack<ArrayList<Integer>> stack = new Stack<ArrayList<Integer>>();
         stack.push(position);
         ArrayList<ArrayList<Integer>> ally_members = new ArrayList<ArrayList<Integer>>();
         while(stack.isEmpty()==false){
             position = stack.pop();
             ally_members.add(position);
             ArrayList<ArrayList<Integer>> neighbor_allies = detect_neighbor_ally(current_board,position, turn);
             for(ArrayList<Integer> x: neighbor_allies) {
                 if(stack.contains(x)==false && ally_members.contains(x)==false){
                     stack.push(x);
                 }
             }
         }
         return ally_members;
     }
     public static ArrayList<ArrayList<Integer>> find_liberty(int[][] current_board, ArrayList<Integer> position, int turn){
         ArrayList<ArrayList<Integer>> ally_members = ally_dfs(current_board, position, turn);
         ArrayList<ArrayList<Integer>> libertylist = new ArrayList<ArrayList<Integer>>();
         for(ArrayList<Integer> member: ally_members){
             ArrayList<ArrayList<Integer>> neighbors =  detect_neighbor(current_board,member,turn);
             for(ArrayList<Integer> piece: neighbors) {
                
                 if(current_board[piece.get(0)][piece.get(1)]==0) {
                     libertylist.add(piece);
                 }
             }
         }
         return libertylist;
     }
     
     public static ArrayList<ArrayList<Integer>> find_died_pieces(int[][] current_board, int piece_type){
        ArrayList<ArrayList<Integer>> deadpieces = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> liberties = new ArrayList<ArrayList<Integer>>();
        for(int i=0;i<5;i++) {
            for(int j=0;j<5;j++) {
                ArrayList<Integer> position = new ArrayList<Integer>();
                if(current_board[i][j]==piece_type){
                    position.add(i);
                    position.add(j);
                    liberties = find_liberty(current_board,position,piece_type);
                    if(liberties.isEmpty()==true && deadpieces.contains(position)==false){
                        deadpieces.add(position);
                    }
                        
                    
                }
            }
        }
        return deadpieces;
     }
     public static int[][] remove_died_pieces(int[][] current_board, ArrayList<ArrayList<Integer>> deadpieces){
         for(ArrayList<Integer> deadpiece : deadpieces) {
             current_board[deadpiece.get(0)][deadpiece.get(1)]= 0;
         }
         return current_board;
     }
     public static boolean state_comparison(int[][] current_state, int[][] previous_state) {
         for(int i=0;i<5;i++) {
             for(int j=0;j<5;j++){
                 if(current_state[i][j]!=previous_state[i][j])
                     return false;
             }
         }
         return true;
     }
     public static ArrayList<ArrayList<Integer>> findvalidmoves(int[][] current_board, int[][] previous_board, int turn){
         ArrayList<ArrayList<Integer>> allmoves = new ArrayList<ArrayList<Integer>>();
         ArrayList<ArrayList<Integer>> deadpieces = new ArrayList<ArrayList<Integer>>();
         ArrayList<ArrayList<Integer>> validmoves = new ArrayList<ArrayList<Integer>>();
         allmoves = findallmoves(current_board);
         int[][] curr_copy = new int[5][5];
         int[][] curr_copy_1 = new int[5][5];
         int[][] curr_copy_2 = new int[5][5];
         for(int i=0;i<allmoves.size();i++){
             for(int j=0;j<5;j++){
                 for(int k=0;k<5;k++){
                     curr_copy[j][k] = current_board[j][k];
                     curr_copy_1[j][k] = current_board[j][k];
                     curr_copy_2[j][k] = current_board[j][k];
                     
                 }
             }
             
             ArrayList<Integer> position= allmoves.get(i);
             curr_copy[position.get(0)][position.get(1)]=turn;
             curr_copy_1[position.get(0)][position.get(1)] = turn;
             curr_copy_2[position.get(0)][position.get(1)] = turn;
             ArrayList<ArrayList<Integer>> libertylist = find_liberty(curr_copy, position, turn);
             if(libertylist.isEmpty()==true) {
                 deadpieces = find_died_pieces(curr_copy_1,3-turn);
                 if(deadpieces.isEmpty()==false){
                     curr_copy_1 = remove_died_pieces(curr_copy_1,deadpieces);
                 }
                  libertylist = find_liberty(curr_copy_1, position, turn);
             }
             if(libertylist.isEmpty()==false){
                 deadpieces = find_died_pieces(curr_copy_2,3-turn);
                 if(deadpieces.isEmpty()==false){
                     curr_copy_2 = remove_died_pieces(curr_copy_2,deadpieces);
                     if(state_comparison(curr_copy_2,previous_board)){
                         continue;
                     }else {
                         validmoves.add(position);
                        
                     }
                 }else {
                     validmoves.add(position);
                 }
                 
             }
             
         }
         return validmoves;
        
     }

     public static ArrayList<Integer> findmove(int[][] g_current_board, int[][] g_previous_board, int turn){
             int[][] curr_copy = new int[5][5];
             int[][] prev_copy = new int[5][5];
             for(int j=0;j<5;j++){
                 for(int k=0;k<5;k++){
                     curr_copy[j][k] =g_current_board[j][k];
                     prev_copy[j][k] = g_previous_board[j][k];
                     
                 }
             }
         ArrayList<Integer> position = new ArrayList<Integer>();
        ArrayList<ArrayList<Integer>> validmoves = new ArrayList<ArrayList<Integer>>();
        validmoves = findvalidmoves(curr_copy, prev_copy, turn);
        if(validmoves.size()==0) {
            position.add(-1);
            position.add(-1);
            return position;
        }
        int depth = 0;
        
        position = alpha_beta_search(curr_copy, prev_copy,validmoves,depth);
        return position; 
     }
     public static int[][] copy_array(int[][] arry){
         int[][] temp = new int[5][];
         for(int i=0;i<5;i++) {
             temp[i]=arry[i].clone();
         }
         return temp;
     }
     public static ArrayList<Integer> alpha_beta_search(int[][] current_board, int[][] previous_board, ArrayList<ArrayList<Integer>> validmoves, int depth) {
         double result = 0;
         int step = currentstep;
         ArrayList<Integer> nextmove = new ArrayList<Integer>();
         double v = Integer.MIN_VALUE;
         nextmove = new ArrayList<Integer>();
         nextmove.add(-1);
         nextmove.add(-1);
         for(ArrayList<Integer> position: validmoves){
             int[][] old_copy = copy_array(current_board);
             current_board[position.get(0)][position.get(1)]=globalturn;
             int[][] future_copy= copy_array(current_board);
             ArrayList<ArrayList<Integer>> died_pieces = find_died_pieces(future_copy, 3-globalturn);
             future_copy = remove_died_pieces(future_copy,died_pieces);
             result = min_value(future_copy,old_copy,depth+1,Integer.MIN_VALUE,Integer.MAX_VALUE,step+1);
             current_board[position.get(0)][position.get(1)]=0;
             if(result>v) {
                 v = result;
                 nextmove = position;
             }
         }
         return nextmove;
         
     }
     
     public static double min_value(int[][] current_board,int[][] previous_board,int depth, double alpha, double beta, int step) {
      
         if(depth>=totaldepth){
             return eval(current_board,step);
         }
       
         depth= depth+1;
         ArrayList<ArrayList<Integer>> validmoves = findvalidmoves(current_board,previous_board,3-globalturn);
         double val = Integer.MAX_VALUE;
         for(ArrayList<Integer> position: validmoves){  
             int[][] old_copy = copy_array(current_board);
             current_board[position.get(0)][position.get(1)]=3-globalturn;
             int[][] future_copy= copy_array(current_board);
             ArrayList<ArrayList<Integer>> died_pieces = find_died_pieces(future_copy, globalturn);
             future_copy = remove_died_pieces(future_copy,died_pieces);
             double result = max_value(future_copy,old_copy,depth,alpha,beta,step+1);
             current_board[position.get(0)][position.get(1)]=0;
             val = Math.min(result,val);
             if(val<=alpha) {
                 return val;    

             }
             beta = Math.min(val, beta);
         }
         return val;
         
     }
     public static double max_value(int[][] current_board,int[][] previous_board,int depth, double alpha, double beta,int step) {
        
         if(depth>=totaldepth){
             return eval(current_board,step);
         }
         
         depth= depth+1;    
         ArrayList<ArrayList<Integer>> validmoves = findvalidmoves(current_board,previous_board,globalturn);
         double val = Integer.MIN_VALUE;
         for(ArrayList<Integer> position: validmoves){  
             int[][] old_copy = copy_array(current_board);
             current_board[position.get(0)][position.get(1)]=globalturn;
             int[][] future_copy= copy_array(current_board);
             ArrayList<ArrayList<Integer>> died_pieces = find_died_pieces(future_copy, 3-globalturn);
             future_copy = remove_died_pieces(future_copy,died_pieces);
             double result = min_value(future_copy,old_copy,depth,alpha,beta,step+1);
             current_board[position.get(0)][position.get(1)]=0;
             val = Math.max(result,val);
             if(val>=beta) {
                 return val;    

             }
             alpha= Math.max(val, alpha);
         }
         return val;
         
     }
     public static int countNumberOfPieces(int[][] current_board){
         int piececount =0;
         for(int i=0;i<5;i++) {
             for(int j=0;j<5;j++) {
                 if(current_board[i][j]!=0)
                     piececount++;
             }
         }
         return piececount;
     }

     public static void logSteps(int steps) throws IOException{

         FileWriter stepupdater = new FileWriter("logsteps.txt");
         stepupdater.write(String.valueOf(steps));
         stepupdater.close();
     }
     public static int stepgetter(int piececount) throws IOException {
//           try{
             File getter = new File("logsteps.txt") ;
             Scanner sc = new Scanner(getter);
             int steps = sc.nextInt();
             sc.close();
             return steps;
             }
//           catch(FileNotFoundException e){
//                 System.out.println("Can't read");
//             }
//            return 0;
//}
     public static void generateoutput(String position) {
         try {
             BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
             writer.write(position+"\n");
             writer.close();
           }
        
         catch (IOException e) {
              e.printStackTrace();
       }
         return;
     }
     
     public static void main(String[] args) throws IOException{
         g_previous_board = new int[5][5];
         g_current_board = new int[5][5];
         totalmoves = 24;
         globalturn=0;
         totaldepth=3;
         int steps = 0;
         ArrayList<Integer> position = new ArrayList<Integer>();
         try {
             File inputfile = new File("input.txt");
             Scanner scan = new Scanner(inputfile);
             String turnstr = scan.nextLine();
             globalturn = Integer.parseInt(turnstr);
             for(int i=0;i<5;i++) {
                 String inp = scan.nextLine();
                 for(int j=0;j<5;j++) {
                     g_previous_board[i][j] = Character.getNumericValue(inp.charAt(j));
                 }
             }
             for(int i=0;i<5;i++) {
                 String inp = scan.nextLine();
                 for(int j=0;j<5;j++) {
                     g_current_board[i][j] = Character.getNumericValue(inp.charAt(j));
                 }
             }scan.close();
         }catch (IOException e) {
             e.printStackTrace();
           }
         int piececount = countNumberOfPieces(g_current_board);
         int prevpiececount = countNumberOfPieces(g_previous_board);
         if(piececount==0) {
             logSteps(1);
             position.add(2);
             position.add(2);
         }else if(prevpiececount==0){
             logSteps(2);
             if(g_current_board[2][2]==0) {
                 position.add(2);
                 position.add(2);
             }else {
                 position.add(2);
                 position.add(1);
             }
         }
         else {
            steps = stepgetter(piececount);
            currentstep=steps+1;
            steps = steps+2;
            logSteps(steps);
            eval(g_current_board,1);
            position = findmove(g_current_board, g_previous_board, globalturn);
            
         }
         if(position.get(0)==-1 && position.get(1)==-1) {
             generateoutput("PASS");
            
         }
         else{
            String nextmove = String.valueOf(position.get(0))+","+String.valueOf(position.get(1));
            generateoutput(nextmove);
         }
         
        
     }
     
}