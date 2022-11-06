package pl.baluch.stickergenerator.auth;

import io.dekorate.docker.annotation.DockerBuild;
import io.dekorate.kubernetes.annotation.ImagePullPolicy;
import io.dekorate.kubernetes.annotation.KubernetesApplication;
import io.dekorate.kubernetes.annotation.Port;
import io.dekorate.kubernetes.annotation.Probe;
import io.micronaut.runtime.Micronaut;

@KubernetesApplication(
        name = "stickergeneratorauthservice",
        ports = @Port(name = "grpc", containerPort = 50051),
        imagePullPolicy = ImagePullPolicy.Always,
        livenessProbe = @Probe(grpcAction = "50051", initialDelaySeconds = 5),
        readinessProbe = @Probe(grpcAction = "50051", initialDelaySeconds = 5)
)
@DockerBuild(group = "xopyip", name = "stickergeneratorauthservice")
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
