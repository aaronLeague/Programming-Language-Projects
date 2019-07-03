in_rectangle([CX,CY], [X,Y,Width,Height]) :- CX > X, CX =< X + Width, CY > Y, CY =< Y + Height.
