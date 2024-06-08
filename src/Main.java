import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RHI rhi = new RHI(RHI.DebugModes.BEAUTY);
            JFrame frame = new JFrame("3D Model Drawer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(rhi.width, rhi.height);

            Model model = new Model();
            try {
                // model.load("models/lowsphere.obj");
                // model.load("models/cat.obj");
                // model.load("models/cube_diorama/cube_diorama.obj");
                model.load("models/girl.obj");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            rhi.RegistMesh(model);
            RenderTarget modelPanel = new RenderTarget(rhi);
            modelPanel.setBackground(Color.CYAN);
            frame.setBackground(Color.CYAN);
            frame.add(modelPanel);
            frame.setVisible(true);
        });
    }
}
