import java.util.*;
import java.lang.*;
import java.io.*;
import java.math.*;

/**
*  @Auther : Jaswant Singh [jaswant.singh@practo.com, jaswantsinghyadav007@gmail.com]
*  @Date   : Tue Jun  6 23:08:33 IST 2017
*  @Algorithm: Persistent Data Structure : Persistent Trie Implementation/ IMMUTABLE DATA STRUCTURE
*  <Time Complexity, Space Complexity> : < O(Q * Log N , O(N * Log N))>   Q: No of Queries , N : No of Nodes
*  @Desc : Preprocessing < O(N log N) , O(N log N)> and O(L) per query. L = trie depth
*/


class Node {

  public Node children[];
  public Node() {
    children = new Node[2];
    children[0] = null;
    children[1] = null;
  }
  public Node(int a, int b) {
    children = new Node[2];
    if(a > 0)children[0] = new Node();
    if(b > 0)children[1] = new Node();
  }
  @Override
  public String toString() {
    return "zeros :=> " + (children[0] != null) + " ,ones :=> " + (children[1] != null) + "\n";
  }

}

class A {

  private InputStream inputStream ;
  private OutputStream outputStream ;
  private FastReader in ;
  private PrintWriter out ;
  /*
    Overhead [Additional Temporary Storage] but provides memory reusability for multiple test cases.
  */

  //Critical Size Limit : 10^5 + 4

    private final int BUFFER = 3;
    private int    tempints[] = new int[BUFFER];
    private long   templongs[] = new long[BUFFER];
    private double tempdoubles[] = new double[BUFFER];
    private char   tempchars[] = new char[BUFFER];
    private final long mod = 1000000000 + 7;
    private final int  INF  = Integer.MAX_VALUE / 10;
    private final long INF_L  = Long.MAX_VALUE / 100;

    public A() {}
    public A(boolean stdIO)throws FileNotFoundException {
  //stdIO = false;
      if (stdIO) {
        inputStream = System.in;
        outputStream = System.out;
      } else {
        inputStream = new FileInputStream("input.txt");
        outputStream = new FileOutputStream("output.txt");
      }
      in = new FastReader(inputStream);
      out = new PrintWriter(outputStream);
    }

    int n, Q;
    int curr_n = 0;
    int root_id ;
    int root_key ;
    HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
    int key[];
    int id[];
    int f[];
  Node TRIE[];                   // Immutable Trie for each Node

  void run()throws Exception {

    n = i(); Q = i();
    key  = new int[200000 + 1];
    id   = new int[200000 + 1];
    f    = new int[200000 + 1];
    TRIE  = new Node[200000 + 1];

    root_id = i(); root_key = i();
    id[1] = root_id;
    key[1] = root_key;
    hm.put(root_id, ++curr_n);

    for (int i = 0; i <= 200000 ; i++) {
      TRIE[i] = new Node();
    }

    buildTrie(1);

    for (int i = 1; i <= n - 1; i++) {
      int a = i(); int b = i(); int k = i();
      int u = get(a); int v = get(b);
      if (u == -1) {
        hm.put(a, ++curr_n);
        u = curr_n;
        id[curr_n] = a;
      }
      if (v == -1) {
        hm.put(b, ++curr_n);
        v = curr_n;
        id[curr_n] = b;
      }
      key[u] = k;   // u <-- v   u(k)
      f[u] = v;
      buildTrie(u);     // set father and build IMMUTABLE TRIE : Persistant DS
    }

    int last_answer = 0;
    for (int i = 1; i <= Q; i++) {
      long t = l();
      t ^= last_answer;
      if (t == 0) {
        int u = i(); int v = i(); int k = i();
        u ^= last_answer;
        v ^= last_answer;
        k ^= last_answer;
        hm.put(v, ++curr_n);
        key[curr_n] = k;
        id[curr_n] = v;
        v = curr_n;
        f[v] = get(u);
        buildTrie(v);
      }else {
        int v = i(); int k = i();
        v ^= last_answer;
        k ^= last_answer;
        v = get(v);
        int min_answer = getMinTrie(getFixedLengthString(Integer.toBinaryString(k)), TRIE[v], k);
        int max_answer = getMaxTrie(getFixedLengthString(Integer.toBinaryString(k)), TRIE[v], k);
        out.write("" + min_answer + " " + max_answer + "\n");
        out.flush();
        // update last_answer
        last_answer = min_answer ^ max_answer;
     }
     out.flush();
   }
    //out.write("Case #"+tt+": "+res+"\n");
    //}//end tests
    }//end run

    int get(int id) {
      Integer I = (Integer)hm.get(id);
      if (I == null)return -1;
      else return I.intValue();
    }


    void buildTrie(int u)throws Exception {
      TRIE[u] = new Node();
    addToImmutableTrie(getFixedLengthString(Integer.toBinaryString(key[u])), TRIE[f[u]], TRIE[u]); // IMMutability : add to parent
  }

  String s = "0000000000000000000000000000000000";
  String getFixedLengthString(String x) {
    return s.substring(x.length()) + x;
  }
  // Path Copy Logic
  // Never change the old copy i.e. Mutability
  void addToImmutableTrie(String x, Node old, Node copy)throws Exception {

    Node child_old = null;
    Node child_copy = null;
    for (int pos = 0; pos <= x.length() - 1; pos++) {
      if(copy==null)copy = new Node();

      if (x.charAt(pos) == '0') {
        if(old == null){
        copy.children[0] = new Node();                    // Key always have to make new node along copy path
        copy.children[1] = null;
      }else if(old.children[0]==null){
          copy.children[0] = new Node();                 // Key always have to make new node along copy path
          copy.children[1] =  old.children[1];
        }else{
          copy.children[0] = new Node(1,0);//old.children[0];  // Key always have to make new node along copy path
          copy.children[1] = old.children[1];
        }
        child_copy = copy.children[0];
        if(old == null || old.children[0]==null)child_old = null;
        else child_old = old.children[0];
      }else {
        if(old == null){
          copy.children[0] = null;
          copy.children[1] = new Node();
        }else if(old.children[0]==null){
          copy.children[0] = old.children[0];
          copy.children[1] = new Node();
        }else{
          copy.children[0] = old.children[0];
          copy.children[1] = new Node(0, 1);//old.children[1];
        }
        child_copy = copy.children[1];
        if(old == null || old.children[1]==null)child_old = null;
        else child_old = old.children[1];
      }
      copy = child_copy;
      old = child_old;
    }
  }

  int getMinTrie(String x, Node root, int k)throws Exception {
    Node curr = root;
    long min = 0;
    for (int i = 0; i <= x.length() - 1; i++) {
      Node child;
      char inv = x.charAt(i) == '0' ? '1' : '0';
      Node cnt = curr.children[x.charAt(i)-'0'];
      if (cnt != null) {
        min = ( min << 1 ) + 0;
        child = curr.children[x.charAt(i)-'0'];
      } else {
        min = ( min << 1 ) + 1;
        child = curr.children[inv-'0'];
      }
      curr = child;
    }
    return (int)min;
  }

  int getMaxTrie(String x, Node root, int k)throws Exception {
    Node curr = root;
    long max = 0;
    for (int i = 0; i <= x.length() - 1; i++) {
      Node child;
      char inv = x.charAt(i) == '0' ? '1' : '0';
      Node cnt = curr.children[inv-'0'];
      if (cnt != null) {
        max = ( max << 1 ) + 1;
        child = curr.children[inv-'0'];
      } else {
        max = ( max << 1 ) + 0;
        child = curr.children[x.charAt(i)-'0'];
      }
      curr = child;
    }
    return (int)max;
  }
  void print_r(Node p)throws Exception {
    Node q = p;
    LinkedList<Node> queue = new LinkedList<Node>();
    queue.add(q); 
    while(!queue.isEmpty()){
      q = (Node)queue.removeFirst();
      out.write(""+q+" ");
      out.flush(); 
      if(q.children[0] != null)queue.add(q.children[0]);
      if(q.children[1] != null)queue.add(q.children[1]); 
    }
    out.write("\n");
    out.flush();
  }

  int hash(String s) {
    int base = 31;
    int a = 31;//base = a multiplier
    int mod = 100005;//range [0..100004]
    long val = 0;
    for (int i =  1 ; i <= s.length() ; i++) {
      val += base * s.charAt(i - 1);
      base = ( a * base ) % 100005;
    }
    return (int)(val % 100005) ;
  }

  boolean isPrime(long n) {
    if (n == 1)return false;
    if (n <= 3)return true;
    if (n % 2 == 0)return false;
    for (int i = 2 ; i <= Math.sqrt(n); i++) {
      if (n % i == 0)return false;
    }
    return true;
  }
// sieve
  int[] sieve(int n) {

    boolean isPrime[] = new boolean[n + 1];
    int p[] = new int[n + 1];
    int idx = 1;
    // Put above 3 variables globle p[1..idx-1]


    Arrays.fill(isPrime, true);
    isPrime[0] = isPrime[1] = false;
    for (int i = 2 ; i <= n ; i++) {
      if (isPrime[i]) {
        p[idx++] = i;
        for (int j  = 2 * i ; j <= n ; j += i ) {
          isPrime[j] = false;
        }

      }

    }
    return p;
  }
  long gcd(long a , long b) {
    if (b == 0)return a;
    return gcd(b , a % b);
  }
  long lcm(long a , long b) {
    if (a == 0 || b == 0)return 0;
    return (a * b) / gcd(a, b);
  }
  long mulmod(long a , long b , long mod) {
    if (a == 0 || b == 0)return 0;
    if (b == 1)return a;
    long ans = mulmod(a, b / 2, mod);
    ans = (ans * 2) % mod;
    if (b % 2 == 1)ans = (a + ans) % mod;
    return ans;
  }
  long pow(long a , long b , long mod) {
    if (b == 0)return 1;
    if (b == 1)return a;
    long ans = pow(a, b / 2, mod);
    ans = (ans * ans);
    if (ans >= mod )ans %= mod;

    if (b % 2 == 1)ans = (a * ans);
    if (ans >= mod )ans %= mod;

    return ans;
  }
  // 20*20   nCr Pascal Table
  long[][] ncrTable() {
    long ncr[][] = new long[21][21];
    for (int i = 0 ; i <= 20 ; i++){ncr[i][0] = 1; ncr[i][i] = 1;}

      for (int j = 0; j <= 20 ; j++) {
        for (int i = j + 1; i <= 20 ; i++) {
          ncr[i][j] = ncr[i - 1][j] + ncr[i - 1][j - 1];
        }
      }
      return ncr;
    }
//*******************************I/O******************************//
    int i()throws Exception {
  //return Integer.parseInt(br.readLine().trim());
      return in.nextInt();
    }
    int[] is(int n)throws Exception {
  //int arr[] = new int[n+1];
      for (int i = 1 ; i <= n ; i++)tempints[i] = in.nextInt();
        return tempints;
    }
    long l()throws Exception {
      return in.nextLong();
    }
    long[] ls(int n)throws Exception {
      for (int i = 1 ; i <= n ; i++)templongs[i] = in.nextLong();
        return templongs;
    }

    double d()throws Exception {
      return in.nextDouble();
    }
    double[] ds(int n)throws Exception {
      for (int i = 1 ; i <= n ; i++)tempdoubles[i] = in.nextDouble();
        return tempdoubles;
    }
    char c()throws Exception {
      return in.nextCharacter();
    }
    char[] cs(int n)throws Exception {
      for (int i = 1 ; i <= n ; i++)tempchars[i] = in.nextCharacter();
        return tempchars;
    }
    String s()throws Exception {
      return in.nextLine();
    }
    BigInteger bi()throws Exception {
      return in.nextBigInteger();
    }
//***********************I/O ENDS ***********************//
//*********************** 0.3%f [precision]***********************//
/* roundoff upto 2 digits
   double roundOff = Math.round(a * 100.0) / 100.0;
                    or
   System.out.printf("%.2f", val);

*/
/*
  print upto 2 digits after decimal
  val = ((long)(val * 100.0))/100.0;

*/ 

  private void closeResources() {
    out.flush();
    out.close();
    return;
  }
  public static void main(String[] args) throws java.lang.Exception {
  //let_me_start Shinch Returns


  /*
      // Old Reader Writer
      BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
      BufferedWriter out=new BufferedWriter(new OutputStreamWriter(System.out));
  BufferedReader br=new BufferedReader(new FileReader("input.txt"));
      BufferedWriter out=new BufferedWriter(new FileWriter("output.txt"));
  */

      A driver = new A(true);
      long start =  System.currentTimeMillis();
      driver.run();
      long end =  System.currentTimeMillis();
      driver.closeResources();
      return ;

    }

  }

  class FastReader {

    private boolean finished = false;

    private InputStream stream;
    private byte[] buf = new byte[4 * 1024];
    private int curChar;
    private int numChars;
    private SpaceCharFilter filter;

    public FastReader(InputStream stream) {
      this.stream = stream;
    }

    public int read() {
      if (numChars == -1) {
        throw new InputMismatchException ();
      }
      if (curChar >= numChars) {
        curChar = 0;
        try {
          numChars = stream.read (buf);
        } catch (IOException e) {
          throw new InputMismatchException ();
        }
        if (numChars <= 0) {
          return -1;
        }
      }
      return buf[curChar++];
    }

    public int peek() {
      if (numChars == -1) {
        return -1;
      }
      if (curChar >= numChars) {
        curChar = 0;
        try {
          numChars = stream.read (buf);
        } catch (IOException e) {
          return -1;
        }
        if (numChars <= 0) {
          return -1;
        }
      }
      return buf[curChar];
    }

    public int nextInt() {
      int c = read ();
      while (isSpaceChar (c))
        c = read ();
      int sgn = 1;
      if (c == '-') {
        sgn = -1;
        c = read ();
      }
      int res = 0;
      do {
        if (c == ',') {
          c = read();
        }
        if (c < '0' || c > '9') {
          throw new InputMismatchException ();
        }
        res *= 10;
        res += c - '0';
        c = read ();
      } while (!isSpaceChar (c));
      return res * sgn;
    }

    public long nextLong() {
      int c = read ();
      while (isSpaceChar (c))
        c = read ();
      int sgn = 1;
      if (c == '-') {
        sgn = -1;
        c = read ();
      }
      long res = 0;
      do {
        if (c < '0' || c > '9') {
          throw new InputMismatchException ();
        }
        res *= 10;
        res += c - '0';
        c = read ();
      } while (!isSpaceChar (c));
      return res * sgn;
    }

    public String nextString() {
      int c = read ();
      while (isSpaceChar (c))
        c = read ();
      StringBuilder res = new StringBuilder ();
      do {
        res.appendCodePoint (c);
        c = read ();
      } while (!isSpaceChar (c));
      return res.toString ();
    }

    public boolean isSpaceChar(int c) {
      if (filter != null) {
        return filter.isSpaceChar (c);
      }
      return isWhitespace (c);
    }

    public static boolean isWhitespace(int c) {
      return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == -1;
    }

    private String readLine0() {
      StringBuilder buf = new StringBuilder ();
      int c = read ();
      while (c != '\n' && c != -1) {
        if (c != '\r') {
          buf.appendCodePoint (c);
        }
        c = read ();
      }
      return buf.toString ();
    }

    public String nextLine() {
      String s = readLine0 ();
      while (s.trim ().length () == 0)
        s = readLine0 ();
      return s;
    }

    public String nextLine(boolean ignoreEmptyLines) {
      if (ignoreEmptyLines) {
        return nextLine ();
      } else {
        return readLine0 ();
      }
    }

    public BigInteger nextBigInteger() {
      try {
        return new BigInteger (nextString ());
      } catch (NumberFormatException e) {
        throw new InputMismatchException ();
      }
    }

    public char nextCharacter() {
      int c = read ();
      while (isSpaceChar (c))
        c = read ();
      return (char) c;
    }

    public double nextDouble() {
      int c = read ();
      while (isSpaceChar (c))
        c = read ();
      int sgn = 1;
      if (c == '-') {
        sgn = -1;
        c = read ();
      }
      double res = 0;
      while (!isSpaceChar (c) && c != '.') {
        if (c == 'e' || c == 'E') {
          return res * Math.pow (10, nextInt ());
        }
        if (c < '0' || c > '9') {
          throw new InputMismatchException ();
        }
        res *= 10;
        res += c - '0';
        c = read ();
      }
      if (c == '.') {
        c = read ();
        double m = 1;
        while (!isSpaceChar (c)) {
          if (c == 'e' || c == 'E') {
            return res * Math.pow (10, nextInt ());
          }
          if (c < '0' || c > '9') {
            throw new InputMismatchException ();
          }
          m /= 10;
          res += (c - '0') * m;
          c = read ();
        }
      }
      return res * sgn;
    }

    public boolean isExhausted() {
      int value;
      while (isSpaceChar (value = peek ()) && value != -1)
        read ();
      return value == -1;
    }

    public String next() {
      return nextString ();
    }

    public SpaceCharFilter getFilter() {
      return filter;
    }

    public void setFilter(SpaceCharFilter filter) {
      this.filter = filter;
    }

    public interface SpaceCharFilter {
      public boolean isSpaceChar(int ch);
    }
  }

  class Pair implements Comparable<Pair> {

    public int a;
    public int b;

    public Pair() {
      this.a = 0;
      this.b = 0;
    }
    public Pair(int a, int b) {
      this.a = a;
      this.b = b;
    }
    public int compareTo(Pair p) {
      if (this.b < p.b)return -1;
      else if (this.b > p.b )return 1;
      else {
        if (this.a < p.a)return -1;
        else if (this.a > p.a)return 1;
        else return 0;
      }
    }
    public String toString() {
      return "a=" + this.a + " b=" + this.b;
    }

  }
