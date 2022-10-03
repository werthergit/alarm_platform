package ap;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class MetaInAlarm {

    private    String id0;
    private    String name;

    private   String metricsName;


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MetaInAlarm that = (MetaInAlarm) o;
        return getId0().equals(that.getId0()) && getId0().equals(that.getId0());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId0(), getId0());
    }
}
