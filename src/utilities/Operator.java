package utilities;

public enum Operator
{
    ADD{
        @Override public int doArith(int x1, int x2) {
            return x1 + x2;
        }
    },
    SUB{
        @Override public int doArith(int x1, int x2) {
            return x1 - x2;
        }
    },
    MULT{
        @Override public int doArith(int x1, int x2) {
            return x1 * x2;
        }
    },
    AND{
        @Override public int doArith(int x1, int x2) {
            return (x1 & x2);
         }
    },
    OR{
        @Override public int doArith(int x1, int x2) {
            return (x1 | x2);
         };
    };
    
    // Yes, enums *can* have abstract methods. This code compiles...
    public abstract int doArith(int x1, int x2);
}
