package umonaz.omnibus.auth.domain;

public interface AuthenticationToken<T> {
    T getUserId();
}
