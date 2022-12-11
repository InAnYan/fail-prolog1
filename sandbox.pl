parent(c, a).
parent(c, b).

good(a).
good(g).
good(c).

bad(b).
bad(c).
bad(g).

awful(X) :- bad(X).

very_bad(X) :- awful(X), good(X).