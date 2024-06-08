import java.awt.Color;

class Point3D {
    public double x = 0, y = 0, z = 0;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Point3D Clamp(double minimam, double maximum) {
        Point3D retval = new Point3D();
        retval.x = Math.max(minimam, Math.min(maximum, this.x));
        retval.y = Math.max(minimam, Math.min(maximum, this.y));
        retval.z = Math.max(minimam, Math.min(maximum, this.z));
        return retval;
    }

    public Point3D Clamp(Point3D minimam, Point3D maximum) {
        Point3D retval = new Point3D();
        retval.x = Math.max(minimam.x, Math.min(maximum.x, this.x));
        retval.y = Math.max(minimam.y, Math.min(maximum.y, this.y));
        retval.z = Math.max(minimam.z, Math.min(maximum.z, this.z));
        return retval;
    }

    public Point3D transform(Mat4 matrix) {
        double[][] mt4 = matrix.getMatrix();
        double newX = mt4[0][0] * x + mt4[0][1] * y + mt4[0][2] * z + mt4[0][3];
        double newY = mt4[1][0] * x + mt4[1][1] * y + mt4[1][2] * z + mt4[1][3];
        double newZ = mt4[2][0] * x + mt4[2][1] * y + mt4[2][2] * z + mt4[2][3];
        return new Point3D(newX, newY, newZ);
    }

    public Point3D Normalize() {
        double dist = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (dist != 0.0) {
            return new Point3D(this.x / dist, this.y / dist, this.z / dist);
        } else {
            // Handle the case where the vector has zero length.
            // You might want to throw an exception or return a default vector here.
            return new Point3D(0.0, 0.0, 0.0); // Return default vector
        }
    }

    public double Magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Color tocolor() {
        Point3D tmp = this.Normalize();
        tmp = Point3D.multiply(this.Clamp(0, 1), new Point3D(255, 255, 255));
        return new Color((int) tmp.x, (int) tmp.y, (int) tmp.z);
    }

    public static Point3D project(Point3D point3D, int width, int height, double scale) {
        double distance = 4;
        double factor = distance / (distance - point3D.z);
        int x = (int) (point3D.x * factor * scale + width / 2);
        int y = (int) (-point3D.y * factor * scale + height / 2);
        return new Point3D(x, y, point3D.z);
    }

    public static Point3D subtract(Point3D p1, Point3D p2) {
        return new Point3D(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
    }

    public static Point3D add(Point3D p1, Point3D p2) {
        return new Point3D(p1.x + p2.x, p1.y + p2.y, p1.z + p2.z);
    }

    public static Point3D multiply(Point3D p1, Point3D p2) {
        return new Point3D(p1.x * p2.x, p1.y * p2.y, p1.z * p2.z);
    }

    public static Point3D multiply(Point3D p1, double p) {
        return new Point3D(p1.x * p, p1.y * p, p1.z * p);
    }

    public static Point3D crossProduct(Point3D v1, Point3D v2) {
        return new Point3D(
                v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.x);
    }

    public static double dotProduct(Point3D v1, Point3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }
}
