package sri.os.utility;

public class QuadTree {

	private QuadTree[] nodes;

	private int size;
	private double x, y, radius;

	private Object data;

	public QuadTree(Object data, double x, double y){
		nodes = new QuadTree[4];
		this.data = data;
		this.x = x;
		this.y = y;
	}


	public void add(QuadTree tree){

		size++;
		double distance = distance(tree);
		radius = Math.max(radius, distance+tree.radius);

		int idx = getIndex(tree);
		QuadTree child = nodes[idx];

		if(child == null){
			nodes[idx] = tree;
		}else{		
			child.add(tree);			
		}
	}


	public QuadTree findClosest(double x, double y, double rad){

		QuadTree result = this;
		return findClosest(x,y,rad,result);
	}


	private QuadTree findClosest(double x, double y, double rad, QuadTree t){

		double distance = distance(x, y);

		if(distance  > radius+rad){
			return t;
		}

		if(distance < t.distance(x,y)){
			t = this;
		}

		int index = getIndex(x,y);

		if(nodes[index] != null){

			t = nodes[index].findClosest(x,y,rad,t);
		}else{

			for(int i = 0; i < 4; i++){

				QuadTree tr = nodes[i];

				if(tr != null){
					t = tr.findClosest(x,y,rad,t);
				}
			}
		}

		return t;
	}


	public Object getData(){
		return data;
	}


	public double getX(){
		return x;
	}


	public double getY(){
		return y;
	}


	public double getRadius(){
		return radius;
	}


	public int getSize(){
		return size;
	}


	private int getIndex(QuadTree tree){

		return getIndex(tree.x,tree.y);
	}


	private int getIndex(double x, double y){

		int dx = this.x > x ? 1 : 0;
		int dy = this.y > y ? 1 : 0;

		return dx + dy*2;
	}


	private double distance(double x, double y){

		double dx = this.x - x;
		double dy = this.y - y;

		return Math.sqrt(dx*dx + dy*dy);
	}


	private double distance(QuadTree tree){
		return distance(tree.x,tree.y);
	}

}
