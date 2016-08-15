package sri.util;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import sri.entity.Entity;
import sri.entity.Polygon;
import sri.physics.PhysTree;


public class VectorMath {

	
	public static final Vector3f AXIS_PITCH = new Vector3f(1,0,0);
	public static final Vector3f AXIS_YAW   = new Vector3f(0,1,0);
	public static final Vector3f AXIS_ROLL  = new Vector3f(0,0,1);
	
	private Vector3f vec3_a, vec3_b, vec3_c;
	private Matrix4f matrice;
	
	public VectorMath(){
		
		vec3_a = new Vector3f(0,0,0);
		vec3_b = new Vector3f(0,0,0);
		vec3_c = new Vector3f(0,0,0);
		
		matrice = new Matrix4f();
	}
	

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static float distance(PhysTree a, Vector3f b){

		return distance(a.getPos().x,a.getPos().y,a.getPos().z,b.x,b.y,b.z);
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static float distance(PhysTree a, PhysTree b){

		return distance(a.getPos().x,a.getPos().y,a.getPos().z,b.getPos().x,b.getPos().y,b.getPos().z);
	}


	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static float distance(Vector3f a, Vector3f b){

		return distance(a.x,a.y,a.z,b.x,b.y,b.z);
	}

	/**
	 * 
	 * @param x0
	 * @param y0
	 * @param z0
	 * @param x1
	 * @param y1
	 * @param z1
	 * @return
	 */
	public static float distance(float x0, float y0, float z0, float x1, float y1, float z1){

		float dx = x0-x1;
		float dy = y0-y1;
		float dz = z0-z1;

		return (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
	}


	/**
	 * 
	 * @param point
	 * @param pos
	 * @param dir
	 * @param distance
	 * @return
	 */
	public static boolean isInLine(Vector3f point, Vector3f pos, Vector3f dir, float distance){

		float dist = distance(pos,point);

		if( dist < distance && distance(pos.x + dir.x*dist, pos.y + dir.y*dist, pos.z + dir.z*dist, point.x, point.y, point.z) < 30){
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param point
	 * @param pos
	 * @param dir
	 * @param distance
	 * @param radius
	 * @return
	 */
	public static boolean isInLine(Vector3f point, Vector3f pos, Vector3f dir, float distance, float radius){

		float dist = distance(pos,point);

		if( dist < distance+radius && distance(pos.x + dir.x*dist, pos.y + dir.y*dist, pos.z + dir.z*dist, point.x, point.y, point.z) < radius){
			return true;
		}

		return false;
	}

	/**
	 * Returns true if the point+radius is in the sphere given by the position and radius
	 * @param point
	 * @param size
	 * @param pos
	 * @param radius
	 * @return
	 */
	public static boolean isInSphere(Vector3f point, float size, Vector3f pos, float radius){

		if(distance(pos,point) < radius+size){
			return true;
		}

		return false;
	}

	/**
	 * Returns true if the point is in the sphere given by the position and radius
	 * @param point
	 * @param pos
	 * @param radius
	 * @return
	 */
	public static boolean isInSphere(Vector3f point, Vector3f pos, float radius){

		if(distance(pos,point) < radius){
			return true;
		}

		return false;
	}

	
	/**
	 * Returns true if the two planes intersect
	 * @param p0
	 * @param p1
	 * @return
	 */
	public boolean planeIntersection(Polygon p0, Polygon p1){
	  	
		Vector3f n0 = p0.getNormal();
	    Vector3f n1 = p1.getNormal();
	    Vector3f.cross(n0, n1, vec3_a);

	    float c0 = n0.x*p0.getV1().x + n0.y*p0.getV1().y  + n0.z*p0.getV1().z;
	    float c1 = n1.x*p1.getV1().x + n1.y*p1.getV1().y  + n1.z*p1.getV1().z;
	    
	    float kn = n1.x / n0.x;
	    
	    vec3_b.setY((c1 - c0*kn) / (n1.y - n0.y * kn) );
	    vec3_b.setX((c0 - n0.y*vec3_b.y)/n1.x );
	    vec3_b.setZ(0);

	    Vector3f.add(vec3_b, vec3_a, vec3_c);

	    
	    Vector3f.sub(vec3_c, p0.getV1(), vec3_a);
	    float i0 = Vector3f.dot(vec3_a, n0);
	    
	    Vector3f.sub(vec3_c, p1.getV1(), vec3_b);
	    float i1 = Vector3f.dot(vec3_b, n1);
	    
	    return Math.abs(i0) < 0.01f && Math.abs(i1) < 0.01f;
	}
	
	/**
	 * Returns true of the two polygons intersect.
	 * @param p1
	 * @param p2
	 * @return
	 */
	public boolean polygonIntersection(Polygon p1, Polygon p2){
				
		Vector3f n1 = p1.getNormal();
		Vector3f n2 = p2.getNormal();
		
	
		n2.negate(vec3_a);
		float d2 = Vector3f.dot(vec3_a, p2.getV1());
		
		float d2v1 = Vector3f.dot(n2, p1.getV1()) + d2;
		float d2v2 = Vector3f.dot(n2, p1.getV2()) + d2;
		float d2v3 = Vector3f.dot(n2, p1.getV3()) + d2;
		
		if(isSigned(d2v1) == isSigned(d2v2) == isSigned(d2v3)){
			return false;
		}
		
		
		n1.negate(vec3_a);
		float d1 = Vector3f.dot(vec3_a, p1.getV1());
		
		float d1v1 = Vector3f.dot(n1, p2.getV1()) + d1;
		float d1v2 = Vector3f.dot(n1, p2.getV2()) + d1;
		float d1v3 = Vector3f.dot(n1, p2.getV3()) + d1;
		
		if(isSigned(d1v1) == isSigned(d1v2) == isSigned(d1v3)){
			return false;
		}
		
		
		Vector3f D = vec3_b;
		Vector3f.cross(n1, n2, D);
			
		
		float p1v1 = Vector3f.dot(D, p1.getV1());
		float p1v2 = Vector3f.dot(D, p1.getV2());
		float p1v3 = Vector3f.dot(D, p1.getV3());
		
		float p2v1 = Vector3f.dot(D, p2.getV1());
		float p2v2 = Vector3f.dot(D, p2.getV2());
		float p2v3 = Vector3f.dot(D, p2.getV3());
		
		
		float p1t1 = p1v1 + (p1v2 - p1v1)*d1v1/(d1v1-d1v2);
		float p1t2 = p1v3 + (p1v2 - p1v3)*d1v3/(d1v3-d1v2);
		
		float p2t1 = p2v1 + (p2v2 - p2v1)*d2v1/(d2v1-d2v2);
		float p2t2 = p2v3 + (p2v2 - p2v3)*d2v3/(d2v3-d2v2);
		
		
	
		if(p1t1 < p2t1 || p1t2 < p2t2){
			return false;
		}	
		
		return true;
	}
	
	
	private static boolean isSigned(float f){
		return f >= 0.0f;	
	}
	
	
	
	/**
	 * 
	 * @param p
	 * @param dest
	 */
	public void calculateNormalFromVertexes(Polygon p, Vector3f dest){


		vec3_a.set(p.getV2().x - p.getV1().x, p.getV2().y - p.getV1().y, p.getV2().z - p.getV1().z);
		
		vec3_b.set(p.getV3().x - p.getV1().x, p.getV3().y - p.getV1().y, p.getV3().z - p.getV1().z);
		
		Vector3f.cross(vec3_a, vec3_b, dest);


		if(dest.length() > 0){
			dest.normalise();
		}
		
	}

	

	/**
	 * Translates a global vector into the localized vector from the input Entity
	 * @param e
	 * @param in
	 * @param dest
	 */
	public void toLocal(Entity e, Vector3f in, Vector3f dest){

		matrice.setIdentity();

		matrice.m30 = in.x;
		matrice.m31 = in.y;
		matrice.m32 = in.z;

		vec3_a.set(-e.pos.x,-e.pos.y,-e.pos.z);
		Matrix4f.translate(vec3_a, matrice, matrice);

		matrice.m00 = matrice.m30;
		matrice.m10 = matrice.m31;
		matrice.m20 = matrice.m32;

		
		matrice.rotate(e.angle.y,AXIS_YAW);
		matrice.rotate(e.angle.x,AXIS_PITCH);
		matrice.rotate(e.angle.z,AXIS_ROLL);	

		dest.set(
				matrice.m00,
				matrice.m10,
				matrice.m20
				);
	}	
	
	
	
	


	public void toLocalDir(Entity e, Vector3f in, Vector3f dest){

		matrice.setIdentity();

		matrice.m00 = in.x;
		matrice.m10 = in.y;
		matrice.m20 = in.z;

		matrice.rotate(e.angle.y,AXIS_YAW);
		matrice.rotate(e.angle.x,AXIS_PITCH);
		matrice.rotate(e.angle.z,AXIS_ROLL);	


		dest.set(
				matrice.m00,
				matrice.m10,
				matrice.m20
				);


		if(dest.length() > 0){
			dest.normalise();
		}
	}	


	public static void toWorld(Entity e, Vector3f in, Vector3f dest){

		Vector3f up = e.getUp();
		
		if(up==null){
			return;
		}
		
		
		Vector3f pos = e.getPos();
		Vector3f right = e.getRight();
		Vector3f forward = e.getForward();		
			
		
		dest.set(
				pos.x +	in.x*right.x + in.y*up.x + in.z*forward.x,
				pos.y +	in.x*right.y + in.y*up.y + in.z*forward.y,
				pos.z +	in.x*right.z + in.y*up.z + in.z*forward.z
				);
	}


	
	public void setAngle(Entity e, float x, float y, float z){

		e.angle.set(x, y, z);

		matrice.setIdentity();

		matrice.rotate(e.angle.y,VectorMath.AXIS_YAW);
		matrice.rotate(e.angle.x,VectorMath.AXIS_PITCH);
		matrice.rotate(e.angle.z,VectorMath.AXIS_ROLL);

		e.getRight().set(matrice.m00, matrice.m01, matrice.m02);
		e.getUp().set(matrice.m10, matrice.m11, matrice.m12);
		e.getForward().set(matrice.m20, matrice.m21, matrice.m22);
	}
	
	
	
	public static int getRGB(int red, int green, int blue, int alpha){
			
		return (red << 24) | (green << 16) | (blue << 8) | alpha;
	}
	
	
	

}
