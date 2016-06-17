import java.util.*;

public class ConvexHull {
    public List<Point> findConvexHull(Point[] points) {        
        if(points.length == 0) return new ArrayList<Point>();
        
        // 1. find the leftmost(lowest) point P0
        double x_min = Double.POSITIVE_INFINITY;
        int min = 0;
        for(int i = 0; i < points.length; i++) {
            if(points[i].x < x_min || points[i].x == x_min && points[i].y < points[min].y) {
                x_min = points[i].x; 
                min = i;
            }
        }
        
        // swap point_0 and point_min
        Point tmp = points[0];
        points[0] = points[min];
        points[min] = tmp;
        
        // 2. sort the rest of the points according to their orientation (CCW)
        Arrays.sort(points, 1, points.length, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                int c = checkCCW(points[0], p1, p2);
                if(c == 0) return distance(points[0], p1, p2);
                return c;
            }
        });
                
        // 3. remove co-linear points that are closer to P0. m is the new length of valid points.
        int m = 1;
        for(int i = 1; i < points.length; i++) {
            // remove co-linear points that are closer to P0.
            while(i < points.length - 1 && checkCCW(points[0], points[i], points[i+1]) == 0) i++;
            
            points[m++] = points[i];
        }
        if(m < 3) return new ArrayList<Point>();
        
        // 4. build convex hull from triplets
        List<Point> chList = new ArrayList<Point>();
        chList.add(points[0]);
        chList.add(points[1]);
        chList.add(points[2]);
        
        for(int i = 3; i < m; i++) {
            Point p = chList.get(chList.size() - 2);
            Point q = chList.get(chList.size() - 1);
            Point r = points[i];
            
            while(checkCCW(p, q, r) != -1) {
                chList.remove(chList.size() - 1);
                q = p;
                p = chList.get(chList.size() - 2);
            }
            chList.add(r);
        }
        
        return chList;
    }
    
    private int checkCCW(Point p, Point q, Point r) {
        double comp = (r.y - p.y) * (q.x - p.x) - (r.x - p.x) * (q.y - p.y);
        
        if(comp == 0) return 0;     // co-linear
        else if(comp < 0) return 1; // CW
        else return -1;              // CCW
    }
    
    private int distance(Point p, Point q, Point r) {
        double distq = (q.x - p.x) * (q.x - p.x) + (q.y - p.y) * (q.y - p.y);
        double distr = (r.x - p.x) * (r.x - p.x) + (r.y - p.y) * (r.y - p.y);
        
        if(distq <= distr) return -1;
        else return 1;
    }
    
    private void printPoints(List<Point> list) {
        for(int i = 0; i < list.size(); i++) {
            Point p = list.get(i);
            System.out.println("(" + p.x + "," + p.y + ")");
        }
    }
    
    public static void main(String[] args) {
        ConvexHull ch = new ConvexHull();
        Scanner sc = new Scanner(System.in);
        
        int count = sc.nextInt();
        Point[] points = new Point[count];
        
        for(int i = 0; i < count; i++) {
            double x = sc.nextDouble(), y = sc.nextDouble();
            points[i] = new Point(x, y);
        }
        
        ch.printPoints(ch.findConvexHull(points));
    }
}

class Point {
        double x;
        double y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
