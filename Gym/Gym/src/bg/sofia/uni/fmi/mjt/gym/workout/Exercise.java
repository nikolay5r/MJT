package bg.sofia.uni.fmi.mjt.gym.workout;

import java.util.Objects;

public record Exercise(String name, int sets, int repetitions) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
