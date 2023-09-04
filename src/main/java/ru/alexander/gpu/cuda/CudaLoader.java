package ru.alexander.gpu.cuda;

import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUmodule;

import static jcuda.driver.JCudaDriver.*;

public class CudaLoader {

    private CudaLoader() {}


    public static CUcontext context;
    public static CUdevice device;
    public static CUmodule simulation;
    public static CUmodule render;


    public static void init() {
        setExceptionsEnabled(true);

        cuInit(0);
        device = new CUdevice();
        cuDeviceGet(device, 0);
        context = new CUcontext();
        cuCtxCreate(context, 0, device);

        simulation = new CUmodule();
        cuModuleLoad(simulation, "src/main/resources/simulation.ptx");

        render = new CUmodule();
        cuModuleLoad(render, "src/main/resources/render.ptx");
    }

    public void close() {
        cuCtxDestroy(context);
        cuModuleUnload(simulation);
        cuModuleUnload(render);
    }
}
