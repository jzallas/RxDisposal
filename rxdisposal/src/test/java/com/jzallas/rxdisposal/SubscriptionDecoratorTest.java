package com.jzallas.rxdisposal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SubscriptionDecoratorTest {

    @Spy
    private SubscriptionDecorator mockSubscriptionDecorator;

    @Mock
    private Consumer<String> mockOnNext;

    @Mock
    private Consumer<Throwable> mockOnError;

    @Mock
    private Consumer<Disposable> mockOnSusbcribe;

    @Mock
    private Action mockOnComplete;

    @Mock
    private Observer<String> mockObserver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private Observer<? super String> createLambdaObserver(int args) {
        Observer<? super String> observer;
        switch (args) {
            case 1:
                observer = mockSubscriptionDecorator.wrap(mockOnNext);
                break;
            case 2:
                observer = mockSubscriptionDecorator.wrap(mockOnNext, mockOnError);
                break;
            case 3:
                observer = mockSubscriptionDecorator.wrap(mockOnNext, mockOnError, mockOnComplete);
                break;
            case 4:
                observer = mockSubscriptionDecorator.wrap(mockOnNext, mockOnError, mockOnComplete, mockOnSusbcribe);
                break;
            default:
                observer = mockSubscriptionDecorator.wrap();
                break;
        }

        assertTrue(observer != null);

        return observer;
    }

    private void validateOnNext(Observer<? super String> testObserver) throws Exception {
        String testString = "test";
        testObserver.onNext(testString);
        verify(mockOnNext, times(1))
                .accept(testString);
    }

    private void validateOnError(Observer<? super String> testObserver) throws Exception {
        Throwable testThrowable = mock(Throwable.class);

        testObserver.onError(testThrowable);
        verify(mockOnError, times(1))
                .accept(testThrowable);
    }

    private void validateOnComplete(Observer<? super String> testObserver) throws Exception {
        testObserver.onComplete();
        verify(mockOnComplete, times(1))
                .run();
    }

    private void validateOnSubscribe(Observer<? super String> testObserver) throws Exception {
        final Disposable testDisposable = mock(Disposable.class);
        testObserver.onSubscribe(testDisposable);
        verify(mockOnSusbcribe, times(1))
                .accept(argThat(validateDisposeIsDelegated(testDisposable)));

    }

    /**
     * There's no guarantee that the {@link Disposable} we provided will be the one that is delegated.
     * However, we should be guaranteed that if we call {@link Disposable#dispose()} on the delegated
     * {@link Disposable}, it should also call {@link Disposable#dispose()} on our test {@link Disposable}
     *
     * @param testDisposable
     * @return
     */
    private ArgumentMatcher<Disposable> validateDisposeIsDelegated(final Disposable testDisposable) {
        return new ArgumentMatcher<Disposable>() {
            @Override
            public boolean matches(Disposable disposable) {
                disposable.dispose();
                verify(testDisposable, times(1)).dispose();
                return true;
            }
        };
    }

    private void validateDisposableDelegated(Observer<? super String> testObserver) throws Exception {
        verify(mockSubscriptionDecorator, times(1))
                .delegateDisposable(any(Disposable.class));
    }

    @Test
    public void testWrapNoArgs() {
        Observer observer = createLambdaObserver(0);
        verify(mockSubscriptionDecorator, times(1))
                .delegateDisposable(any(Disposable.class));

        assertTrue(observer != null);
    }

    @Test
    public void testWrap1Arg() throws Exception {
        Observer<? super String> testObserver = createLambdaObserver(1);

        validateDisposableDelegated(testObserver);

        validateOnNext(testObserver);
    }

    @Test
    public void testWrap2Args() throws Exception {
        Observer<? super String> testObserver = createLambdaObserver(2);

        validateDisposableDelegated(testObserver);

        validateOnNext(testObserver);

        validateOnError(testObserver);
    }

    @Test
    public void testWrap3Args() throws Exception {
        Observer<? super String> testObserver = createLambdaObserver(3);

        validateDisposableDelegated(testObserver);

        validateOnNext(testObserver);

        validateOnError(testObserver);

        testObserver = createLambdaObserver(3);

        validateOnComplete(testObserver);
    }

    @Test
    public void testWrap4Args() throws Exception {
        Observer<? super String> testObserver = createLambdaObserver(4);

        validateDisposableDelegated(testObserver);

        validateOnNext(testObserver);

        validateOnError(testObserver);

        testObserver = createLambdaObserver(4);

        validateOnComplete(testObserver);

        testObserver = createLambdaObserver(4);

        validateOnSubscribe(testObserver);
    }

    @Test
    public void testWrapObserver() throws Exception {
        String testString = "test";
        Throwable testThrowable = mock(Throwable.class);
        Disposable testDisposable = mock(Disposable.class);

        Observer<? super String> testObserver = mockSubscriptionDecorator.wrap(mockObserver);
        validateDisposableDelegated(testObserver);

        testObserver.onNext(testString);
        verify(mockObserver, times(1))
                .onNext(testString);

        testObserver.onError(testThrowable);
        verify(mockObserver, times(1))
                .onError(testThrowable);

        testObserver = mockSubscriptionDecorator.wrap(mockObserver);

        testObserver.onComplete();
        verify(mockObserver, times(1))
                .onComplete();

        testObserver = mockSubscriptionDecorator.wrap(mockObserver);

        testObserver.onSubscribe(testDisposable);
        verify(mockObserver, times(1))
                .onSubscribe(argThat(validateDisposeIsDelegated(testDisposable)));
    }
}