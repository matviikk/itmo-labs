import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.itmo.web.lab3.util.AreaChecker;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class AreaCheckerTest {

    private final double x;
    private final double y;
    private final double r;
    private final boolean expected;

    public AreaCheckerTest(double x, double y, double r, boolean expected) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // 1 четверть (треугольник)
                {1, 1, 3, true},
                {2, 2, 3, false},
                // 2 четверть — вне области
                {-1, 1, 3, false},
                // 3 четверть (квадрат)
                {-1, -1, 2, true},
                {-2, -2, 2, true},
                {-3, -3, 2, false},
                // 4 четверть (четверть круга)
                {1, -1, 4, true},
                {2, -2, 4, false},
        });
    }

    @Test
    public void testCheckHit() {
        boolean actual = AreaChecker.checkHit(x, y, r);
        System.out.printf("x=%s, y=%s, r=%s → actual=%s, expected=%s%n", x, y, r, actual, expected);
        assertThat("x=" + x + ", y=" + y + ", r=" + r, actual, is(expected));
    }
}