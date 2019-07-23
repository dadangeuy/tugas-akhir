() -> 42 // No parameters, expression body
() -> { return 42; } // No parameters, block body with return
() -> { System.gc(); } // No parameters, void block body
(x, y) -> x+y // Multiple inferred-type parameters

