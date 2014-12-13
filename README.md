The "distance" between two (word,frequency)-vectors is the angle between them.

If x = ((w1,f1), (w2,f2), ..., (wn,fn)) is the first vector for file_1 and y = ((w1',f'1), (w'2,f'2), ..., (w'm,f'm)) is the second vector for file_2, then the angle between them is defined as:

d(x,y) = arccos(inner_product(x,y) / (norm(x)*norm(y)))

