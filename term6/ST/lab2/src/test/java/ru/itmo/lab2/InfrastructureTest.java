package ru.itmo.lab2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.itmo.lab2.csv.CsvWriter;
import ru.itmo.lab2.functions.Func;
import ru.itmo.lab2.functions.log.LnFunction;
import ru.itmo.lab2.functions.trig.SinFunction;
import ru.itmo.lab2.stubs.TableStub;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Инфраструктура — TableStub и CsvWriter")
class InfrastructureTest {

    @Test
    @DisplayName("TableStub: точный матч, интерполяция, загрузка из classpath")
    void tableStub() throws Exception {

        TableStub lin = new TableStub("lin", Map.of(0.0, 0.0, 1.0, 10.0));
        assertEquals(0.0,  lin.apply(0.0));
        assertEquals(10.0, lin.apply(1.0));
        assertEquals(5.0,  lin.apply(0.5), 1e-15);
        assertThrows(IllegalArgumentException.class, () -> lin.apply(-1.0));

        TableStub sin = TableStub.fromResource("sin", "stubs/sin.csv", ";");
        assertEquals(0.5, sin.apply(Math.PI / 6), 1e-12);
    }

    @Test
    @DisplayName("CsvWriter пишет «X; f(X)» и помечает особенности как undefined")
    void csvWriter(@TempDir Path tmp) throws Exception {
        Path out = tmp.resolve("sin.csv");
        Func sin = new SinFunction(1e-12);
        new CsvWriter(";", "X;sin(X)").write(sin, 0.0, 1.0, 0.25, out);
        var lines = Files.readAllLines(out);
        assertEquals("X;sin(X)", lines.get(0));
        assertEquals(6, lines.size());

        Path lnOut = tmp.resolve("ln.csv");
        new CsvWriter(",", "X,ln(X)").write(new LnFunction(1e-12), -1.0, 1.0, 0.5, lnOut);
        assertTrue(Files.readString(lnOut).contains("undefined"));
    }
}
