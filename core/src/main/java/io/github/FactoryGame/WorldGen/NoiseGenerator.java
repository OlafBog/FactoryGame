package io.github.FactoryGame.WorldGen;

import java.util.Random;

public class NoiseGenerator {
    private final int[] permutation;

    public NoiseGenerator(long seed) {
        permutation = new int[512];
        int[] p = new int[256];

        for (int i = 0; i < 256; i++) {
            p[i] = i;
        }

        Random random = new Random(seed);
        for (int i = 0; i < 256; i++) {
            int j = random.nextInt(256);
            int temp = p[i];
            p[i] = p[j];
            p[j] = temp;
        }

        for (int i = 0; i < 512; i++) {
            permutation[i] = p[i & 255];
        }
    }

    public double noise(double x, double y) {
        // Znajdź jednostkowy kwadrat, w którym znajduje się punkt
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;

        // Znajdź relatywną pozycję punktu wewnątrz kwadratu
        x -= Math.floor(x);
        y -= Math.floor(y);

        // Oblicz krzywe wygładzania (Fade curves) dla x i y
        // Używamy funkcji: 6t^5 - 15t^4 + 10t^3 (gładsze niż standardowe 3t^2 - 2t^3)
        double u = fade(x);
        double v = fade(y);

        // Haszowanie współrzędnych rogów kwadratu
        int A = permutation[X] + Y;
        int AA = permutation[A];
        int AB = permutation[A + 1];
        int B = permutation[X + 1] + Y;
        int BA = permutation[B];
        int BB = permutation[B + 1];

        // Dodawanie zblendowanych wyników z 4 rogów kwadratu
        return lerp(v, lerp(u, grad(permutation[AA], x, y),
                grad(permutation[BA], x - 1, y)),
            lerp(u, grad(permutation[AB], x, y - 1),
                grad(permutation[BB], x - 1, y - 1)));
    }

    // Funkcja wygładzająca (Ken Perlin's smootherstep)
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    // Interpolacja liniowa (Linear Interpolation)
    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    // Obliczanie iloczynu skalarnego wektora gradientu i wektora odległości
    private double grad(int hash, double x, double y) {
        int h = hash & 15; // Bierzemy 4 ostatnie bity hasha
        // Konwertujemy hasz na jeden z 8 kierunków gradientu
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : 0;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    // --- METODA POMOCNICZA: OCTAVES (Dla bardziej naturalnego terenu) ---
    // x, y - współrzędne
    // octaves - ile warstw szumu (np. 4)
    // persistence - jak szybko zanika amplituda (np. 0.5)
    // lacunarity - jak szybko rośnie częstotliwość (np. 2.0)
    public double getOctaveNoise(double x, double y, int octaves, double persistence, double lacunarity) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;  // Używane do normalizacji wyniku do zakresu [0,1]

        for (int i = 0; i < octaves; i++) {
            total += noise(x * frequency, y * frequency) * amplitude;

            maxValue += amplitude;

            amplitude *= persistence;
            frequency *= lacunarity;
        }

        // Zwracamy wynik znormalizowany (w przybliżeniu -1 do 1, potem przeskalowany na 0 do 1)
        return (total / maxValue) + 0.5; // +0.5 przesuwa wynik, żeby był w okolicach 0-1
    }
}
