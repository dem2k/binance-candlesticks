package dem2k;

import java.io.IOException;
import java.time.LocalDate;

public interface Updater {
    public boolean update(LocalDate atDay);

    public void checkAndClean(LocalDate atDay);

    public void export() throws IOException;
}
