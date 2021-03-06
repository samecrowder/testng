package org.testng;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.testng.internal.ClassImpl;
import org.testng.internal.paramhandler.FakeTestContext;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import test.guice.FakeInjector;

@Guice
public final class GuiceHelperTest {

    private final GuiceHelper guiceHelper = new GuiceHelper(new FakeTestContext());

    @Test(description = "GITHUB-2273")
    public void getInjector_spiModule_injectorHasModule() {
        MockInjector injector = (MockInjector) guiceHelper.getInjector(new MockClass(), new MockInjectorFactory());

        assertNotNull(injector);
        Module[] modules = injector.getModules();
        assertNotNull(modules);
        assertEquals(modules.length, 1);
        assertEquals(modules[0], new SampleIModule().getModule());
    }

    private static final class MockInjectorFactory implements IInjectorFactory {
        @Override
        public Injector getInjector(Stage stage, Module... modules) {
            return new MockInjector(modules);
        }
    }

    private static final class MockInjector extends FakeInjector {
        private final Module[] modules;

        public MockInjector(Module[] modules) {
            this.modules = modules;
        }

        public Module[] getModules() {
            return modules;
        }
    }

    private static final class MockClass extends ClassImpl {
        public MockClass() {
            super(new FakeTestContext(), GuiceHelperTest.class, null, (ITest) () -> "GITHUB-2273", null, null, null);
        }
    }
}
