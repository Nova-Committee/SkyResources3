package committee.nova.mods.skyresources3.common.compat.transfer;

public final class EmptyResourceHandler {
    private static final ResourceHandler<?> INSTANCE = new ResourceHandler<>() {
    };

    @SuppressWarnings("unchecked")
    public static <T> ResourceHandler<T> instance() {
        return (ResourceHandler<T>) INSTANCE;
    }

    private EmptyResourceHandler() {
    }
}
