__kernel void 
addCopy(__global double *a, __global const double *b,__global const double *c)
{
	int gid = get_global_id(0);
	a[gid] = b[gid] + c[gid];
}

__kernel void 
add(__global double *a, __global const double *b, const int aoffset, const int boffset)
{
	int gid = get_global_id(0);
	a[gid+aoffset] += b[gid+boffset];
}

__kernel void 
mul(__global double *a, __global const double *b, const int aoffset, const int boffset)
{
	int gid = get_global_id(0);
	a[gid+aoffset] *= b[gid+boffset];
}

__kernel void 
div(__global double *a, __global const double *b, const int aoffset, const int boffset)
{
	int gid = get_global_id(0);
	a[gid+aoffset] /= b[gid+boffset];
}

__kernel void 
sub(__global double *a, __global const double *b, const int aoffset, const int boffset)
{
	int gid = get_global_id(0);
	a[gid+aoffset] -= b[gid+boffset];
}


__kernel void 
scaleAdd_scalar(__global double *a, const int aoffset, const double factor, const double c)
{
	int i = get_global_id(0) + aoffset;
	a[i] = a[i] * factor + c;
}

__kernel void 
scaleAdd_vector(__global double *a, const int aoffset, const double afactor, __global double *b, const int boffset, const double bfactor, const double c)
{
	int i = get_global_id(0);
	a[i+aoffset] = (a[i+aoffset] * afactor) + (b[i+boffset] * bfactor) + c;
}

__kernel void 
addAt(__global double *a, const int offset, const double v)
{
	a[offset] += v;
}

__kernel void 
dotProduct(__global double *res, __global const double *a, __global const double *b, const int aoffset, const int boffset, const int n, const int stride, const int step)
{
	double acc=0.0;
	int row=get_global_id(0);
	int col=get_global_id(1);
	for(int i = 0; i < n; i++) {
		acc+=a[aoffset+i+row*step]*b[boffset+col+i*stride];
	}
	res[row*n+col]=acc;
}

__kernel void 
addOuterProduct(__global double *res, __global const double *a, __global const double *b, const int aoffset, const int boffset, const int n, const int step)
{
	int row=get_global_id(0);
	for(int i = 0; i < n; i++) {
		res[row*step+i]+=a[aoffset+row]*b[boffset+i];
	}
}