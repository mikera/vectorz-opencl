__kernel void 
op_abs(__global double *a, const int offset)
{
	int i = get_global_id(0) + offset;
	a[i] = fabs(a[i]);
}

__kernel void 
op_sqrt(__global double *a, const int offset)
{
	int i = get_global_id(0) + offset;
	a[i] = sqrt(a[i]);
}

__kernel void 
op_sin(__global double *a, const int offset)
{
	int i = get_global_id(0) + offset;
	a[i] = sin(a[i]);
}

__kernel void 
op_cos(__global double *a, const int offset)
{
	int i = get_global_id(0) + offset;
	a[i] = cos(a[i]);
}

__kernel void 
op_exp(__global double *a, const int offset)
{
	int i = get_global_id(0) + offset;
	a[i] = exp(a[i]);
}

__kernel void 
op_log(__global double *a, const int offset)
{
	int i = get_global_id(0) + offset;
	a[i] = log(a[i]);
}
