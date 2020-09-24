package kr.tracom.platform.common.module;

public interface ModuleInterface {
    void init(Object args);
    void startup();
    void shutdown();
}
