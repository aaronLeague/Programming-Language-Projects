inx(1).
inx(2).
inx(3).
inx(4).
inx(5).
inx(6).
inx(7).
inx(8).
inx(9).
inx(10).
inx(11).
inx(12).

iny(1).
iny(2).
iny(3).
iny(4).
iny(5).
iny(6).
iny(7).
iny(8).
iny(9).
iny(10).

%%Possible move patterns
proceed(h,[1,Y,-],h,[CX,Y,_]) :- inx(CX),
	CX > 1.

proceed(h,[X,Y,/],vu,[X,CY,_]) :- iny(CY),
	iny(Y),
	CY > Y.
proceed(vu,[X,Y,/],h,[CX,Y,_]) :- inx(CX), 
	inx(X),
	CX > X.

proceed(h,[X,Y,\],vd,[X,CY,_]) :- iny(CY), 
	iny(Y),
	Y > CY.
proceed(vd,[X,Y,\],h,[CX,Y,_]) :- inx(CX), 
	inx(X),
	CX > X.


%%Allowed spaces
safe([X,Y,_], []) :- inx(X), iny(Y).
safe(C, [[X,Width,Height]|Tail]) :- \+in_rectangle(C, [X,10,Width,Height]),
	safe(C, Tail).
safe(C, [[X,Y,Width,Height]|Tail]) :- \+in_rectangle(C, [X,Y,Width,Height]),
	safe(C, Tail).


%%Allowed paths
safe_path([X,Y,_], [X,Y,_], Obstacles) :- safe([X,Y,_], Obstacles).
safe_path([X,Y,_],[X,CY,_], Obstacles) :- CY > Y,
	SY is Y+1,
	safe([X,Y,_], Obstacles),
	safe_path([X,SY,_], [X,CY,_], Obstacles).
safe_path([X,Y,_],[X,CY,_], Obstacles) :- Y > CY,
	SY is Y-1,
	safe([X,Y,_], Obstacles),
	safe_path([X,SY,_], [X,CY,_], Obstacles).
safe_path([X,Y,_],[CX,Y,_], Obstacles) :- SX is X+1,
	safe([X,Y,_], Obstacles),
	safe_path([SX,Y,_], [CX,Y,_], Obstacles).


%%Checks if coordinates are within a rectangle
in_rectangle([CX,CY,_], [X,Y,Width,Height]) :- CX >= X, 
	CX < X + Width, 
	CY =< Y, 
	CY > Y - Height.


%%User entry point into the software
place_mirrors(L, Obstacles, AnsPath) :- iny(N), 
	N =< 8,
	length(AnsPath,N),
	iny(M),
	find_path(L, h, [[M,6,2,6]|Obstacles], [[1,L,-]], [[1,L,-]|AnsPath]).


%%Recursive path finder
find_path(L, D, Obstacles, [S|_], [S]) :- proceed(D,S,h,[12,L,_]),
	safe_path(S,[12,L,_], Obstacles),
	!.
find_path(L, D, Obstacles, [S|Path], [S|AnsPath]) :- proceed(D,S,CD,NextState), 
	safe_path(S, NextState, Obstacles), 
	find_path(L, CD, Obstacles, [NextState, S | Path], AnsPath).

