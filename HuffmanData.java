public class HuffmanData {
    char character;
    int frequency;

    public HuffmanData(int frequency) {
        this.character = 0; // equivalent to null
        this.frequency = frequency;
    }

    public HuffmanData(char character, int frequency) {
        this.character = character; // equivalent to null
        this.frequency = frequency;
    }

    public String toString() {
        return (this.character + "; " + this.frequency);
    }

}
