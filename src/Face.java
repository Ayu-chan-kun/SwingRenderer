import java.awt.Color;

public class Face {
    public Point3D p0;
    public Point3D p1;
    public Point3D p2;
    public Point3D n0;
    public Point3D n1;
    public Point3D n2;
    public Color shade0;
    public Color shade1;
    public Color shade2;
    public Color shaded = Color.GREEN;
    public Material material;

    Face() {
        this.p0 = new Point3D();
        this.p1 = new Point3D();
        this.p2 = new Point3D();
    }

    public double getFaceDepth() {
        return (this.p0.z + this.p1.z + this.p2.z) / 3;
    }

    Face(Point3D p0, Point3D p1, Point3D p2) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
    }

    Face(Point3D p0, Point3D p1, Point3D p2, Material material) {
        this(p0, p1, p2);
        this.material = material;
    }

    Face(Point3D p0, Point3D p1, Point3D p2, Point3D n0, Point3D n1, Point3D n2, Material material) {
        this(p0, p1, p2, material);
        this.n0 = n0;
        this.n1 = n1;
        this.n2 = n2;
    }

    public Point3D Normal() {
        Point3D v1 = Point3D.subtract(p1, p0).Normalize();
        Point3D v2 = Point3D.subtract(p2, p0).Normalize();
        return Point3D.crossProduct(v1, v2);
    }

    public Point3D Mean() {
        Point3D retval = new Point3D();
        retval.x = (p0.x + p1.x + p2.x) / 3;
        retval.y = (p0.y + p1.y + p2.y) / 3;
        retval.z = (p0.z + p1.z + p2.z) / 3;
        return retval;
    }

    public Face Copy() {
        return new Face(p0, p1, p2, material);
    }

    public Color Shading(LightScape light, Point3D viewDir) {
        Point3D lightDirection = light.direction.Normalize();
        Point3D normal = this.Normal().Normalize();

        // 散乱光
        double dot = Point3D.dotProduct(lightDirection, normal);
        double diffuseIntensity = Math.max(dot, 0);
        // diffuseIntensity = 0;

        // Reflect light direction around normal
        Point3D reflectDir = Point3D.subtract(Point3D.multiply(normal, 2 * dot), lightDirection).Normalize();

        // 拡散光
        // Point3D viewDir = Point3D.subtract(ciewDir, this.Mean()).Normalize();
        viewDir = viewDir.Normalize();
        double specularStrength = 1;
        double specular = Math.pow(Math.max(Point3D.dotProduct(viewDir, reflectDir), 0), 32) * specularStrength;

        // Combine results
        double ambientStrength = 0.1;
        Point3D ambient = Point3D.multiply(light.ambientcolor, ambientStrength);
        Point3D diffuse = Point3D.multiply(light.directionalcolor, diffuseIntensity);
        Point3D specularColor = Point3D.multiply(new Point3D(1, 1, 1), specular);

        Point3D color = Point3D.add(ambient, diffuse);
        color = Point3D.add(color, specularColor);
        color = Point3D.multiply(color, this.material.diffuse);
        color = color.Clamp(new Point3D(0, 0, 0), new Point3D(1, 1, 1));

        this.shaded = color.tocolor();
        return this.shaded;
    }

    public Face transformAndProject(int width, int height, double scale, Mat4 modelMatrix, Mat4 viewMatrix) {
        Face newface = this.Copy();
        newface = transformToWorld(modelMatrix);
        newface = newface.transformToCamera(width, height, scale, viewMatrix);
        return newface;
    }

    public Face transformToCamera(int width, int height, double scale, Mat4 viewMatrix) {
        Face newface = this.Copy();
        Mat4 rotater = viewMatrix.Copy();
        rotater.SetPosition(new Point3D(0, 0, 0));

        newface.p0 = this.p0.transform(viewMatrix);
        newface.p0 = Point3D.project(newface.p0, width, height, scale);

        // newface.p1 = this.p1.transform(modelMatrix);
        newface.p1 = this.p1.transform(viewMatrix);
        newface.p1 = Point3D.project(newface.p1, width, height, scale);

        // newface.p2 = this.p2.transform(modelMatrix);
        newface.p2 = this.p2.transform(viewMatrix);
        newface.p2 = Point3D.project(newface.p2, width, height, scale);

        newface.n0 = this.n0.transform(rotater);
        newface.n1 = this.n1.transform(rotater);
        newface.n2 = this.n2.transform(rotater);
        return newface;
    }

    public Face transformToWorld(Mat4 modelMatrix) {
        Face newface = this.Copy();
        Mat4 rotater = modelMatrix.Copy();
        rotater.SetPosition(new Point3D(0, 0, 0));

        newface.p0 = this.p0.transform(modelMatrix);
        newface.p1 = this.p1.transform(modelMatrix);
        newface.p2 = this.p2.transform(modelMatrix);
        newface.n0 = this.n0.transform(rotater);
        newface.n1 = this.n1.transform(rotater);
        newface.n2 = this.n2.transform(rotater);
        return newface;
    }

    public double Area() {
        Point3D v1 = Point3D.subtract(p1, p0);
        Point3D v2 = Point3D.subtract(p2, p0);

        Point3D cross = Point3D.crossProduct(v1, v2);
        double area = 0.5 * cross.Magnitude();
        return area;
    }

    public int[][] Break() {
        int[][] retval = {
                { (int) p0.x, (int) p1.x, (int) p2.x },
                { (int) p0.y, (int) p1.y, (int) p2.y },
                { (int) p0.z, (int) p1.z, (int) p2.z }
        };
        return retval;
    }

    public Point3D max() {
        double maxX = Math.max(p0.x, Math.max(p1.x, p2.x));
        double maxY = Math.max(p0.y, Math.max(p1.y, p2.y));
        double maxZ = Math.max(p0.z, Math.max(p1.z, p2.z));
        return new Point3D(maxX, maxY, maxZ);
    }

    public Point3D min() {
        double minX = Math.min(p0.x, Math.min(p1.x, p2.x));
        double minY = Math.min(p0.y, Math.min(p1.y, p2.y));
        double minZ = Math.min(p0.z, Math.min(p1.z, p2.z));
        return new Point3D(minX, minY, minZ);
    }
}
