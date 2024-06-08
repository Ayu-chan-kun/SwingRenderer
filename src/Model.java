import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Model {
    public List<Point3D> vertices = new ArrayList<>();
    public List<Point3D> normals = new ArrayList<>();
    public List<int[]> facesvertid = new ArrayList<>();
    public List<int[]> facesnormalid = new ArrayList<>();
    public List<int[]> facesvtid = new ArrayList<>();
    public List<Point3D> colors = new ArrayList<>();
    public List<Face> faces = new ArrayList<>();

    public Mat4 modelMatrix = new Mat4();
    protected Map<String, Material> materials = new HashMap<>();

    Model() {

    }

    Model(Mat4 modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public void load(String objfile) throws IOException {
        List<String> mtlidx = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(objfile))) {
            String line, mtlfile, mtlname = "";

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("mtllib")) {
                    mtlfile = line.split("\\s+")[1];
                    // System.out.println(Paths.get(objfile).getParent().resolve(mtlfile).toString());
                    loadMaterials(Paths.get(objfile).getParent().resolve(mtlfile).toString());
                } else if (line.startsWith("v ")) {
                    String[] tokens = line.split("\\s+");
                    double x = Double.parseDouble(tokens[1]);
                    double y = Double.parseDouble(tokens[2]);
                    double z = Double.parseDouble(tokens[3]);
                    vertices.add(new Point3D(x, y, z));
                    if (tokens.length > 4) {
                        float r = Float.parseFloat(tokens[4]);
                        float g = Float.parseFloat(tokens[5]);
                        float b = Float.parseFloat(tokens[6]);
                        colors.add(new Point3D(r, g, b));
                    }
                } else if (line.startsWith("vn ")) {
                    String[] tokens = line.split("\\s+");
                    double x = Double.parseDouble(tokens[1]);
                    double y = Double.parseDouble(tokens[2]);
                    double z = Double.parseDouble(tokens[3]);
                    normals.add(new Point3D(x, y, z));
                } else if (line.startsWith("usemtl")) {
                    mtlname = line.split("\\s+")[1];
                    // System.out.println(mtlname);
                } else if (line.startsWith("f ")) {
                    String[] tokens = line.split("\\s+");
                    int[] faceVertIds = new int[tokens.length - 1];
                    int[] faceNormalIds = new int[tokens.length - 1];
                    int[] faceTexcoordIds = new int[tokens.length - 1];

                    for (int i = 1; i < tokens.length; i++) {
                        String[] parts = tokens[i].split("/");
                        faceVertIds[i - 1] = Integer.parseInt(parts[0]) - 1;
                        if (parts.length > 1 && !parts[1].isEmpty()) {
                            faceTexcoordIds[i - 1] = Integer.parseInt(parts[1]) - 1;
                        }
                        if (parts.length > 2) {
                            faceNormalIds[i - 1] = Integer.parseInt(parts[2]) - 1;
                        }
                    }
                    facesvertid.add(faceVertIds);
                    facesvtid.add(faceTexcoordIds);
                    facesnormalid.add(faceNormalIds);
                    mtlidx.add(mtlname);
                }

            }
        }
        for (int i = 0; i < facesvertid.size(); i++) {
            int[] face = facesvertid.get(i);
            int[] _facenormalId = facesnormalid.get(i);

            Point3D p0 = vertices.get(face[0]);
            Point3D p1 = vertices.get(face[1]);
            Point3D p2 = vertices.get(face[2]);
            Point3D n0 = normals.get(_facenormalId[0]);
            Point3D n1 = normals.get(_facenormalId[1]);
            Point3D n2 = normals.get(_facenormalId[2]);
            String mtlname = mtlidx.get(i);
            Material mtl = materials.get(mtlname);
            faces.add(new Face(p0, p1, p2, n0, n1, n2, mtl));
            // faces.add(new Face(p0, p1, p2, mtl));

        }
    }

    private void loadMaterials(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            Material currentMaterial = null;
            String currentMaterialName = "";
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("newmtl ")) {
                    currentMaterialName = line.split("\\s+")[1];
                    // Start a new material definition
                    if (currentMaterial != null) {
                        // materials.put(currentMaterialName, currentMaterial);
                    }
                    // currentMaterialName = line.substring(7).trim();
                    currentMaterial = new Material();
                    materials.put(currentMaterialName, currentMaterial);
                } else if (line.startsWith("Kd ")) {
                    // Diffuse color
                    String[] tokens = line.split("\\s+");
                    double r = Double.parseDouble(tokens[1]);
                    double g = Double.parseDouble(tokens[2]);
                    double b = Double.parseDouble(tokens[3]);
                    currentMaterial.diffuse = new Point3D(r, g, b);
                } else if (line.startsWith("Ks ")) {
                    // Diffuse color
                    String[] tokens = line.split("\\s+");
                    double r = Double.parseDouble(tokens[1]);
                    double g = Double.parseDouble(tokens[2]);
                    double b = Double.parseDouble(tokens[3]);
                    currentMaterial.supecular = new Point3D(r, g, b);
                } else if (line.startsWith("Ns ")) {
                    // Diffuse color
                    String[] tokens = line.split("\\s+");
                    double strength = Double.parseDouble(tokens[1]);
                    currentMaterial.supecular = Point3D.multiply(currentMaterial.supecular,
                            new Point3D(strength, strength, strength));
                }
            }

            // Add the last material
            if (currentMaterial != null) {
                materials.put(currentMaterialName, currentMaterial);
            }
        } catch (

        IOException e) {
            // Handle file reading error
            e.printStackTrace();
            throw e; // Rethrow the exception to indicate failure
        }
    }
}
