import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RHI {
    protected List<Face> faces = new ArrayList<>();
    protected List<Model> models = new ArrayList<>();
    protected LightScape light = new LightScape();
    protected int width = 500;
    protected int height = 500;
    protected int scale = 100;
    protected Mat4 viewMatrix = new Mat4();
    protected double nearclip = -5;// TODO::Z深度の順はあってるけど値がおかしい！
    protected double farclip = 1000;
    protected DebugModes DEBUGMODE = DebugModes.BEAUTY;
    protected Point2D.Double depthRange;

    public enum DebugModes {
        BEAUTY, WORLDNORMAL, CAMERANORMAL, DEPTH, DIFFUSE, SPECULAR
    }

    RHI() {

    }

    RHI(DebugModes debugmode) {
        this.DEBUGMODE = debugmode;
    }

    public void RegistMesh(Model model) {
        models.add(model);

    }

    protected void RenderModel(Model model) {
        for (Face face : model.faces) {
            RenderFace(face, model.modelMatrix);
        }
    }

    protected void RenderFace(Face face, Mat4 modelMatrix) {

        // 面を世界基準に変換
        Face worldface = face.transformToWorld(modelMatrix);
        // 面をカメラ基準に変換
        Face cameraface = worldface.transformToCamera(this.width, this.height, this.scale, this.viewMatrix);

        // 法線が裏だったら描画しない
        if (Point3D.dotProduct(cameraface.Normal(), new Point3D(0, 0, 1)) > 0)
            return;

        // カメラの裏側なら描画しない
        double depth = -cameraface.getFaceDepth();
        this.depthRange.x = Math.min(depth, this.depthRange.x);
        this.depthRange.y = Math.max(depth, this.depthRange.y);
        // System.out.println(depthRange);
        if (depth < this.nearclip || this.farclip < depth)
            return;

        Point3D max = cameraface.max();
        Point3D min = cameraface.min();
        // 画面内に頂点が一つもない面は描画しない。
        if (!(0 <= min.x || max.x <= width) && !(0 <= min.y || max.y <= width))
            return;

        // 小さすぎる面は描画しない
        // if (cameraface.Area() < 0.9)
        // return;
        switch (this.DEBUGMODE) {
            case CAMERANORMAL:
                cameraface.shaded = cameraface.Normal().tocolor();
                break;
            case DEPTH:
                float col = (float) Math.max(0, Math.min(depth - depthRange.x / (depthRange.y - depthRange.x), 1));
                cameraface.shaded = new Color(col, col, col);
                break;
            case DIFFUSE:
                cameraface.shaded = cameraface.material.diffuse.tocolor();
                break;
            case BEAUTY:
                cameraface.shaded = worldface.Shading(light,
                        this.viewMatrix.getForwardVector());
                cameraface.shade0 = cameraface.shaded;
                cameraface.shade1 = cameraface.shaded;
                cameraface.shade2 = cameraface.shaded;
                break;
            case SPECULAR:
                cameraface.shaded = cameraface.material.supecular.tocolor();
                break;
            case WORLDNORMAL:
                cameraface.shaded = worldface.Normal().tocolor();
                cameraface.shade0 = cameraface.n0.tocolor();
                cameraface.shade1 = cameraface.n1.tocolor();
                cameraface.shade2 = cameraface.n2.tocolor();
                break;
            default:
                cameraface.shaded = worldface.Shading(light,
                        this.viewMatrix.getForwardVector());
                break;
        }
        // cameraface.shaded = cameraface.albedo;
        this.faces.add(cameraface);

    }

    public void ProjectAll() {
        int numofface = 0;
        depthRange = new Point2D.Double(1000000000, 0);
        this.ClearFaces();
        ProcessStat.TrackStart("頂点変換と陰影処理");
        for (Model model : models) {
            numofface += model.faces.size();
            this.RenderModel(model);
        }
        ProcessStat.TrackEnd("頂点変換と陰影処理");
        ProcessStat.TrackStart("深度別ソート");
        // 遠いもの順にソートし、描画順を正しくする
        faces.sort(Comparator.comparingDouble(face -> face.getFaceDepth()));
        ProcessStat.TrackEnd("深度別ソート");

        System.out.printf("面の総数:%d, 削減後の面の数:%d", numofface, this.faces.size());
    }

    public void ClearFaces() {

        faces = new ArrayList<>();
    }

    public void ClearModels() {
        models = new ArrayList<>();
    }
}
