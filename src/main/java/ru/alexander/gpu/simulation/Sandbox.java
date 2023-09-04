package ru.alexander.gpu.simulation;


import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import ru.alexander.gpu.cuda.CudaLoader;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import static jcuda.driver.JCudaDriver.*;

public class Sandbox {
    private final ParticleList particles = new ParticleList();
    private final WallList walls = new WallList();
    private final AcceleratorList accelerators = new AcceleratorList();
    private final CUfunction move;
    private final CUfunction update;
    private final CUfunction updateWithCollision;
    private final CUfunction clearPic;
    private final CUfunction renderParticlesPic;
    private final CUfunction renderStructuresPic;

    private final CUfunction renderParticlesImg;
    private final CUfunction renderStructuresImg;

    public Sandbox() {
        move = new CUfunction();
        cuModuleGetFunction(move, CudaLoader.simulation, "move");

        update = new CUfunction();
        cuModuleGetFunction(update, CudaLoader.simulation, "update");

        updateWithCollision = new CUfunction();
        cuModuleGetFunction(updateWithCollision, CudaLoader.simulation, "updateWithCollision");


        clearPic = new CUfunction();
        cuModuleGetFunction(clearPic, CudaLoader.render, "clearPic");

        renderParticlesPic = new CUfunction();
        cuModuleGetFunction(renderParticlesPic, CudaLoader.render, "renderParticlesPic");

        renderStructuresPic = new CUfunction();
        cuModuleGetFunction(renderStructuresPic, CudaLoader.render, "renderStructuresPic");


        renderParticlesImg = new CUfunction();
        cuModuleGetFunction(renderParticlesImg, CudaLoader.render, "renderParticlesImg");

        renderStructuresImg = new CUfunction();
        cuModuleGetFunction(renderStructuresImg, CudaLoader.render, "renderStructuresImg");
    }

    public void simulate(double dt) {
        int particleCount = particles.getPosition().getX().size();
        int wallCount = walls.getPosition1().getX().size();
        int acceleratorCount = accelerators.getPosition1().getX().size();

        load();

        cuLaunchKernel(update,
                (int)Math.ceil((double) particleCount / 32f),  1, 1,
                Math.min(32, particleCount), 1, 1,
                0, null,
                Pointer.to(
                        Pointer.to(particles.getPosition().getX().getPointer()),
                        Pointer.to(particles.getPosition().getY().getPointer()),
                        Pointer.to(particles.getSpeed().getX().getPointer()),
                        Pointer.to(particles.getSpeed().getY().getPointer()),

                        Pointer.to(new int[]{ wallCount }),
                        Pointer.to(walls.getPosition1().getX().getPointer()),
                        Pointer.to(walls.getPosition1().getY().getPointer()),
                        Pointer.to(walls.getPosition2().getX().getPointer()),
                        Pointer.to(walls.getPosition2().getY().getPointer()),

                        Pointer.to(new int[]{ acceleratorCount }),
                        Pointer.to(accelerators.getPosition1().getX().getPointer()),
                        Pointer.to(accelerators.getPosition1().getY().getPointer()),
                        Pointer.to(accelerators.getPosition2().getX().getPointer()),
                        Pointer.to(accelerators.getPosition2().getY().getPointer()),
                        Pointer.to(accelerators.getSpeed().getX().getPointer()),
                        Pointer.to(accelerators.getSpeed().getY().getPointer()),

                        Pointer.to(new double[]{ dt })
                ), null
        );
        cuCtxSynchronize();

        cuLaunchKernel(move,
                (int)Math.ceil((double) particleCount / 32f),  1, 1,
                Math.min(32, particleCount), 1, 1,
                0, null,
                Pointer.to(
                        Pointer.to(particles.getPosition().getX().getPointer()),
                        Pointer.to(particles.getPosition().getY().getPointer()),
                        Pointer.to(particles.getSpeed().getX().getPointer()),
                        Pointer.to(particles.getSpeed().getY().getPointer()),

                        Pointer.to(new double[]{ dt })
                ), null
        );
        cuCtxSynchronize();

        unload();
    }
    public void simulateWithCollision(double dt, double radius, double particleCollisionMultiplier) {
        int particleCount = particles.getPosition().getX().size();
        int wallCount = walls.getPosition1().getX().size();
        int acceleratorCount = accelerators.getPosition1().getX().size();

        load();


        cuLaunchKernel(updateWithCollision,
                (int)Math.ceil((double) particleCount / 32f),  1, 1,
                Math.min(32, particleCount), 1, 1,
                0, null,
                Pointer.to(
                        Pointer.to(new int[]{ particleCount }),
                        Pointer.to(particles.getPosition().getX().getPointer()),
                        Pointer.to(particles.getPosition().getY().getPointer()),
                        Pointer.to(particles.getSpeed().getX().getPointer()),
                        Pointer.to(particles.getSpeed().getY().getPointer()),
                        Pointer.to(new double[]{ radius }),
                        Pointer.to(new double[]{ particleCollisionMultiplier }),

                        Pointer.to(new int[]{ wallCount }),
                        Pointer.to(walls.getPosition1().getX().getPointer()),
                        Pointer.to(walls.getPosition1().getY().getPointer()),
                        Pointer.to(walls.getPosition2().getX().getPointer()),
                        Pointer.to(walls.getPosition2().getY().getPointer()),

                        Pointer.to(new int[]{ acceleratorCount }),
                        Pointer.to(accelerators.getPosition1().getX().getPointer()),
                        Pointer.to(accelerators.getPosition1().getY().getPointer()),
                        Pointer.to(accelerators.getPosition2().getX().getPointer()),
                        Pointer.to(accelerators.getPosition2().getY().getPointer()),
                        Pointer.to(accelerators.getSpeed().getX().getPointer()),
                        Pointer.to(accelerators.getSpeed().getY().getPointer()),

                        Pointer.to(new double[]{ dt })
                ), null
        );
        cuCtxSynchronize();

        cuLaunchKernel(move,
                (int)Math.ceil((double) particleCount / 32f),  1, 1,
                Math.min(32, particleCount), 1, 1,
                0, null,
                Pointer.to(
                        Pointer.to(particles.getPosition().getX().getPointer()),
                        Pointer.to(particles.getPosition().getY().getPointer()),
                        Pointer.to(particles.getSpeed().getX().getPointer()),
                        Pointer.to(particles.getSpeed().getY().getPointer()),

                        Pointer.to(new double[]{ dt })
                ), null
        );
        cuCtxSynchronize();

        unload();
    }
    public void load() {
        particles.getPosition().getX().load();
        particles.getPosition().getY().load();
        particles.getSpeed().getX().load();
        particles.getSpeed().getY().load();


        walls.getPosition1().getX().load();
        walls.getPosition1().getY().load();
        walls.getPosition2().getX().load();
        walls.getPosition2().getY().load();

        accelerators.getPosition1().getX().load();
        accelerators.getPosition1().getY().load();
        accelerators.getPosition2().getX().load();
        accelerators.getPosition2().getY().load();
        accelerators.getSpeed().getX().load();
        accelerators.getSpeed().getY().load();
    }

    public void unload() {
        particles.getPosition().getX().unload();
        particles.getPosition().getY().unload();
        particles.getSpeed().getX().unload();
        particles.getSpeed().getY().unload();
    }

    public BufferedImage renderImage(int width, int height,
                                     double x, double y, double scale) {
        int particleCount = particles.getPosition().getX().size();
        int wallCount = walls.getPosition1().getX().size();
        int acceleratorCount = accelerators.getPosition1().getX().size();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        CUdeviceptr dataPtr = new CUdeviceptr();
        cuMemAlloc(dataPtr, (long) data.length * Sizeof.BYTE);

        load();

        cuLaunchKernel(renderParticlesImg,
                (int)Math.ceil((double) particleCount / 32f),  1, 1,
                Math.min(32, particleCount), 1, 1,
                0, null,
                Pointer.to(
                        Pointer.to(particles.getPosition().getX().getPointer()),
                        Pointer.to(particles.getPosition().getY().getPointer()),

                        Pointer.to(new double[]{ x }),
                        Pointer.to(new double[]{ y }),
                        Pointer.to(new double[]{ scale }),
                        Pointer.to(new int[]{ width }),
                        Pointer.to(new int[]{ height }),
                        Pointer.to(dataPtr)
                ), null
        );
        cuCtxSynchronize();


        cuLaunchKernel(renderStructuresImg,
                (int)Math.ceil((double) width / 32f),  (int)Math.ceil((double) height / 32f), 1,
                Math.min(32, width),  Math.min(32, height), 1,
                0, null,
                Pointer.to(
                        Pointer.to(new int[]{ wallCount }),
                        Pointer.to(walls.getPosition1().getX().getPointer()),
                        Pointer.to(walls.getPosition1().getY().getPointer()),
                        Pointer.to(walls.getPosition2().getX().getPointer()),
                        Pointer.to(walls.getPosition2().getY().getPointer()),

                        Pointer.to(new int[]{ acceleratorCount }),
                        Pointer.to(accelerators.getPosition1().getX().getPointer()),
                        Pointer.to(accelerators.getPosition1().getY().getPointer()),
                        Pointer.to(accelerators.getPosition2().getX().getPointer()),
                        Pointer.to(accelerators.getPosition2().getY().getPointer()),

                        Pointer.to(new double[]{ x }),
                        Pointer.to(new double[]{ y }),
                        Pointer.to(new double[]{ scale }),
                        Pointer.to(new int[]{ width }),
                        Pointer.to(new int[]{ height }),
                        Pointer.to(dataPtr)
                ), null
        );
        cuCtxSynchronize();

        cuMemcpyDtoH(Pointer.to(data), dataPtr, (long) data.length * Sizeof.BYTE);

        return image;
    }
    public Picture renderPicture(int width, int height,
                                 double x, double y, double scale) {
        int particleCount = particles.getPosition().getX().size();
        int wallCount = walls.getPosition1().getX().size();
        int acceleratorCount = accelerators.getPosition1().getX().size();

        Picture image = Picture.create(width, height, ColorSpace.RGB);
        byte[] data = image.getPlaneData(0);

        CUdeviceptr dataPtr = new CUdeviceptr();
        cuMemAlloc(dataPtr, (long) data.length * Sizeof.BYTE);

        load();

        cuLaunchKernel(clearPic,
                (int)Math.ceil((double) width / 32f),  (int)Math.ceil((double) height / 32f), 1,
                Math.min(32, width),  Math.min(32, height), 1,
                0, null,
                Pointer.to(
                        Pointer.to(new int[]{ width }),
                        Pointer.to(new int[]{ height }),
                        Pointer.to(dataPtr)
                ), null
        );
        cuCtxSynchronize();


        cuLaunchKernel(renderParticlesPic,
                (int)Math.ceil((double) particleCount / 32f),  1, 1,
                Math.min(32, particleCount), 1, 1,
                0, null,
                Pointer.to(
                        Pointer.to(particles.getPosition().getX().getPointer()),
                        Pointer.to(particles.getPosition().getY().getPointer()),

                        Pointer.to(new double[]{ x }),
                        Pointer.to(new double[]{ y }),
                        Pointer.to(new double[]{ scale }),
                        Pointer.to(new int[]{ width }),
                        Pointer.to(new int[]{ height }),
                        Pointer.to(dataPtr)
                ), null
        );
        cuCtxSynchronize();


        cuLaunchKernel(renderStructuresPic,
                (int)Math.ceil((double) width / 32f),  (int)Math.ceil((double) height / 32f), 1,
                Math.min(32, width),  Math.min(32, height), 1,
                0, null,
                Pointer.to(
                        Pointer.to(new int[]{ wallCount }),
                        Pointer.to(walls.getPosition1().getX().getPointer()),
                        Pointer.to(walls.getPosition1().getY().getPointer()),
                        Pointer.to(walls.getPosition2().getX().getPointer()),
                        Pointer.to(walls.getPosition2().getY().getPointer()),

                        Pointer.to(new int[]{ acceleratorCount }),
                        Pointer.to(accelerators.getPosition1().getX().getPointer()),
                        Pointer.to(accelerators.getPosition1().getY().getPointer()),
                        Pointer.to(accelerators.getPosition2().getX().getPointer()),
                        Pointer.to(accelerators.getPosition2().getY().getPointer()),

                        Pointer.to(new double[]{ x }),
                        Pointer.to(new double[]{ y }),
                        Pointer.to(new double[]{ scale }),
                        Pointer.to(new int[]{ width }),
                        Pointer.to(new int[]{ height }),
                        Pointer.to(dataPtr)
                ), null
        );
        cuCtxSynchronize();

        cuMemcpyDtoH(Pointer.to(data), dataPtr, (long) data.length * Sizeof.BYTE);
        cuMemFree(dataPtr);

        return image;
    }

    public ParticleList getParticles() {
        return particles;
    }

    public WallList getWalls() {
        return walls;
    }

    public AcceleratorList getAccelerators() {
        return accelerators;
    }
}
