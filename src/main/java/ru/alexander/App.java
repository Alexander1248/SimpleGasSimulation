package ru.alexander;

import org.jcodec.api.SequenceEncoder;
import ru.alexander.gpu.cuda.CudaLoader;
import ru.alexander.gpu.simulation.AcceleratorList;
import ru.alexander.gpu.simulation.ParticleList;
import ru.alexander.gpu.simulation.Sandbox;
import ru.alexander.gpu.simulation.WallList;
import ru.alexander.vector.Vector2;

import java.io.File;
import java.io.IOException;

public class App {

    //      cd C:\Projects\JavaProjects\SimpleGasSimulation\src\main\resources
    //      nvcc -ptx -m64 -arch=native simulation.cu -o simulation.ptx
    //      nvcc -ptx -m64 -arch=native render.cu -o render.ptx


    public static void main(String[] args) throws IOException {
        CudaLoader.init();

        Sandbox sandbox = new Sandbox();
        ParticleList particles = sandbox.getParticles();
        for (double x = -40; x < 40; x += 2)
            for (double y = -40; y < 40; y += 2)
                particles.add(
                        new Vector2(x, y),
                        new Vector2(Math.random() * 2 - 1, Math.random() * 2 - 1));


        WallList walls = sandbox.getWalls();

        walls.add(new Vector2(-50, -50), new Vector2(50, -50));
        walls.add(new Vector2(50, -50), new Vector2(50, 50));
        walls.add(new Vector2(50, 50), new Vector2(-50, 50));
        walls.add(new Vector2(-50, 50), new Vector2(-50, -50));


        AcceleratorList accelerators = sandbox.getAccelerators();
        accelerators.add(new Vector2(-35, -45), new Vector2(-25, -35), new Vector2(1, 0));
        accelerators.add(new Vector2(25, 35), new Vector2(35, 45), new Vector2(-1, 0));


//        for (int i = 0; i < 1000; i++)
//            sandbox.simulate(1e-1);
//
//
//        ImageIO.write(sandbox.renderImage(1000, 1000, 0, 0, 5),
//                "png", new File("test0.png"));
//
//        AWTUtil.savePicture(sandbox.renderPicture(1000, 1000, 0, 0, 5),
//                "png", new File("test.png"));

        SequenceEncoder encoder = SequenceEncoder.createSequenceEncoder(new File("test.mp4"), 60);
        for (int i = 0; i < 10000; i++) {
//            sandbox.simulate(1e-1);
            sandbox.simulateWithCollision(1e-1, 1, 1);
            encoder.encodeNativeFrame(sandbox.renderPicture(500, 500, 0, 0, 4));
            System.out.println(i);
        }
        encoder.finish();
    }
}
