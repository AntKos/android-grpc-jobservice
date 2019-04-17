package io.grpc;

import javax.annotation.Nullable;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class NameResolverFactory {
    /**
     * The port number used in case the target or the underlying naming system doesn't provide a
     * port number.
     *
     * @deprecated this will be deleted along with {@link #newNameResolver(URI, Attributes)} in
     *             a future release.
     *
     * @since 1.0.0
     */
    @Deprecated
    public static final Attributes.Key<Integer> PARAMS_DEFAULT_PORT =
            Attributes.Key.create("params-default-port");

    /**
     * If the NameResolver wants to support proxy, it should inquire this {@link ProxyDetector}.
     * See documentation on {@link ProxyDetector} about how proxies work in gRPC.
     *
     * @deprecated this will be deleted along with {@link #newNameResolver(URI, Attributes)} in
     *             a future release
     */
    @ExperimentalApi("https://github.com/grpc/grpc-java/issues/5113")
    @Deprecated
    public static final Attributes.Key<ProxyDetector> PARAMS_PROXY_DETECTOR =
            Attributes.Key.create("params-proxy-detector");

    @Deprecated
    private static final Attributes.Key<SynchronizationContext> PARAMS_SYNC_CONTEXT =
            Attributes.Key.create("params-sync-context");

    /**
     * Creates a {@link NameResolver} for the given target URI, or {@code null} if the given URI
     * cannot be resolved by this factory. The decision should be solely based on the scheme of the
     * URI.
     *
     * @param targetUri the target URI to be resolved, whose scheme must not be {@code null}
     * @param params optional parameters. Canonical keys are defined as {@code PARAMS_*} fields in
     *               {@link NameResolver.Factory}.
     *
     * @deprecated Implement {@link #newNameResolver(URI, NameResolver.Helper)} instead.  This is
     *             going to be deleted in a future release.
     *
     * @since 1.0.0
     */
    @Nullable
    @Deprecated
    public NameResolver newNameResolver(URI targetUri, final Attributes params) {
        NameResolver.Helper helper = new NameResolver.Helper() {
            @Override
            public int getDefaultPort() {
                return checkNotNull(params.get(PARAMS_DEFAULT_PORT), "default port not available");
            }

            @Override
            public ProxyDetector getProxyDetector() {
                return checkNotNull(params.get(PARAMS_PROXY_DETECTOR), "proxy detector not available");
            }

            @Override
            public SynchronizationContext getSynchronizationContext() {
                return checkNotNull(params.get(PARAMS_SYNC_CONTEXT), "sync context not available");
            }
        };
        return newNameResolver(targetUri, helper);
    }

    /**
     * Creates a {@link NameResolver} for the given target URI, or {@code null} if the given URI
     * cannot be resolved by this factory. The decision should be solely based on the scheme of the
     * URI.
     *
     * @param targetUri the target URI to be resolved, whose scheme must not be {@code null}
     * @param helper utility that may be used by the NameResolver implementation
     *
     * @since 1.19.0
     */
    // TODO(zhangkun83): make this abstract when the other override is deleted
    @Nullable
    public NameResolver newNameResolver(URI targetUri, NameResolver.Helper helper) {
        return newNameResolver(
                targetUri,
                Attributes.newBuilder()
                        .set(PARAMS_DEFAULT_PORT, helper.getDefaultPort())
                        .set(PARAMS_PROXY_DETECTOR, helper.getProxyDetector())
                        .set(PARAMS_SYNC_CONTEXT, helper.getSynchronizationContext())
                        .build());
    }

    /**
     * Returns the default scheme, which will be used to construct a URI when {@link
     * ManagedChannelBuilder#forTarget(String)} is given an authority string instead of a compliant
     * URI.
     *
     * @since 1.0.0
     */
    public abstract String getDefaultScheme();
}
