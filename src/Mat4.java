public class Mat4 {
    protected double[][] matrix4 = new double[4][4];

    Mat4() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.matrix4[i][j] = (i == j) ? 1 : 0;
            }
        }
    }

    // コピーコンストラクタ
    Mat4(double[][] matrix4) {
        // 行列をコピー
        this.matrix4 = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.matrix4[i][j] = matrix4[i][j];
            }
        }
    }

    double[][] getMatrix() {
        return this.matrix4;
    }

    public static double[][] multiplyMatrix(double[][] a, double[][] b) {
        double[][] result = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    public void rotate(double angleX, double angleY, double angleZ) {
        double[][] rotationX = {
                { 1, 0, 0, 0 },
                { 0, Math.cos(angleX), -Math.sin(angleX), 0 },
                { 0, Math.sin(angleX), Math.cos(angleX), 0 },
                { 0, 0, 0, 1 }
        };

        double[][] rotationY = {
                { Math.cos(angleY), 0, Math.sin(angleY), 0 },
                { 0, 1, 0, 0 },
                { -Math.sin(angleY), 0, Math.cos(angleY), 0 },
                { 0, 0, 0, 1 }
        };

        double[][] rotationZ = {
                { Math.cos(angleZ), -Math.sin(angleZ), 0, 0 },
                { Math.sin(angleZ), Math.cos(angleZ), 0, 0 },
                { 0, 0, 1, 0 },
                { 0, 0, 0, 1 }
        };

        this.matrix4 = Mat4.multiplyMatrix(rotationX, this.matrix4);
        this.matrix4 = Mat4.multiplyMatrix(rotationY, this.matrix4);
        this.matrix4 = Mat4.multiplyMatrix(rotationZ, this.matrix4);
    }

    public void SetPosition(Point3D p) {
        this.matrix4[0][3] = p.x;
        this.matrix4[1][3] = p.y;
        this.matrix4[2][3] = p.z;
    }

    public Mat4 Copy() {
        return new Mat4(this.matrix4);
    }

    public Point3D getForwardVector() {
        // 第3列を正面ベクトルとして取得
        return new Point3D(matrix4[0][0], matrix4[1][0], matrix4[2][0]);
    }
}
