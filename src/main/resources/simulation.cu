__device__ int sign(double val) {
    if (val < 0) return -1;
    if (val > 0) return 1;
    return 0;
}

__device__ bool intersection(double p11x, double p11y, double p12x, double p12y,
                            double p21x, double p21y, double p22x, double p22y) {
    double cut1x = p12x - p11x;
    double cut1y = p12y - p11y;

    double prod1 = cut1x * (p21y - p11y) - cut1y * (p21x - p11x);
    double prod2 = cut1x * (p22y - p11y) - cut1y * (p22x - p11x);

    if (sign(prod1) == sign(prod2)) return false;
    double cut2x = p22x - p21x;
    double cut2y = p22y - p21y;

    prod1 = cut2x * (p11y - p21y) - cut2y * (p11x - p21x);
    prod2 = cut2x * (p12y - p21y) - cut2y * (p12x - p21x);
    
    return sign(prod1) != sign(prod2);
}


__device__ void reflect(double& rx, double& ry, double nx, double ny) {
    double mul = 2.0 * (rx * nx + ry * ny) / (nx * nx + ny * ny);
    rx -= nx * mul;
    ry -= ny * mul;
}

extern "C"
__global__ void move(
    double* particlePosX, double* particlePosY, 
    double* particleSpeedX, double* particleSpeedY,    
    double dt) {
    int i = threadIdx.x + blockIdx.x * blockDim.x;

    particlePosX[i] += particleSpeedX[i] * dt;
    particlePosY[i] += particleSpeedY[i] * dt;
}

extern "C"
__global__ void update(
    double* particlePosX, double* particlePosY, 
    double* particleSpeedX, double* particleSpeedY,

    int wallCount,
    double* wallPos1X, double* wallPos1Y,
    double* wallPos2X, double* wallPos2Y,

    int acceleratorCount,
    double* acceleratorPos1X, double* acceleratorPos1Y,
    double* acceleratorPos2X, double* acceleratorPos2Y,
    double* acceleratorSpeedX, double* acceleratorSpeedY,
    
    double dt) {
    int i = threadIdx.x + blockIdx.x * blockDim.x;
    double x = particlePosX[i];
    double y = particlePosY[i];

    for (int j = 0; j < acceleratorCount; j++) {
        double minX = min(acceleratorPos1X[j], acceleratorPos2X[j]);
        double maxX = max(acceleratorPos1X[j], acceleratorPos2X[j]);

        double minY = min(acceleratorPos1Y[j], acceleratorPos2Y[j]);
        double maxY = max(acceleratorPos1Y[j], acceleratorPos2Y[j]);

        if (x > minX && x < maxX && y > minY && y < maxY) {
            particleSpeedX[i] += acceleratorSpeedX[j] * dt;
            particleSpeedY[i] += acceleratorSpeedY[j] * dt;
        }
    }

    bool reflected;
    do {
        reflected = false;
        for (int j = 0; j < wallCount; j++) 
            if (intersection(x, y, x + particleSpeedX[i] * dt, y + particleSpeedY[i] * dt, wallPos1X[j], wallPos1Y[j], wallPos2X[j], wallPos2Y[j])) {
                reflect(particleSpeedX[i], particleSpeedY[i], wallPos1Y[j] - wallPos2Y[j], wallPos2X[j] - wallPos1X[j]);
                reflected = true;
                break;
            }
    } while (reflected);
}

extern "C"
__global__ void updateWithCollision(
    int particleCount,
    double* particlePosX, double* particlePosY, 
    double* particleSpeedX, double* particleSpeedY,
    double particleRadius, double particleCollisionMultiplier,

    int wallCount,
    double* wallPos1X, double* wallPos1Y,
    double* wallPos2X, double* wallPos2Y,

    int acceleratorCount,
    double* acceleratorPos1X, double* acceleratorPos1Y,
    double* acceleratorPos2X, double* acceleratorPos2Y,
    double* acceleratorSpeedX, double* acceleratorSpeedY,
    
    double dt) {
    int i = threadIdx.x + blockIdx.x * blockDim.x;
    double x = particlePosX[i];
    double y = particlePosY[i];

    for (int j = 0; j < acceleratorCount; j++) {
        double minX = min(acceleratorPos1X[j], acceleratorPos2X[j]);
        double maxX = max(acceleratorPos1X[j], acceleratorPos2X[j]);

        double minY = min(acceleratorPos1Y[j], acceleratorPos2Y[j]);
        double maxY = max(acceleratorPos1Y[j], acceleratorPos2Y[j]);

        if (x > minX && x < maxX && y > minY && y < maxY) {
            particleSpeedX[i] += acceleratorSpeedX[j] * dt;
            particleSpeedY[i] += acceleratorSpeedY[j] * dt;
        }
    }

    for (int j = 0; j < particleCount; j++) {
        if (i != j) {
            double dx = x - particlePosX[j] + (particleSpeedX[i] - particleSpeedX[j]) * dt;
            double dy = y - particlePosY[j]  + (particleSpeedY[i] - particleSpeedY[j]) * dt;
            double dst = sqrt(dx * dx + dy * dy);
            if (dst < particleRadius) {
                dx /= dst;
                dy /= dst;
                double vel = sqrt(pow(particleSpeedX[j], 2) + pow(particleSpeedY[j], 2));
                double val = particleCollisionMultiplier * vel * (particleRadius - dst) / particleRadius * dt;
                particleSpeedX[i] += dx * val;
                particleSpeedY[i] += dy* val;
            }
        }
    }

    bool reflected;
    do {
        reflected = false;
        for (int j = 0; j < wallCount; j++) 
            if (intersection(x, y, x + particleSpeedX[i] * dt, y + particleSpeedY[i] * dt, wallPos1X[j], wallPos1Y[j], wallPos2X[j], wallPos2Y[j])) {
                reflect(particleSpeedX[i], particleSpeedY[i], wallPos1Y[j] - wallPos2Y[j], wallPos2X[j] - wallPos1X[j]);
                reflected = true;
                break;
            }
    } while (reflected);
}