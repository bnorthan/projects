#include "washu.h"

extern "C" {

// routine to create volume PSF from XZ cross-section (interp) 
void rotnone(
    float *vol, 
    osm_ds *head
);

// routine to create volume PSF from XZ cross-section (sums nghbr pixels) 
void rotsum(
    float *vol, 
    osm_ds *head
);

// routine to create volume PSF from XZ cross-section for circular confocal 
void rotdiskcirc(
    float *vol, 
    osm_ds *head,
    int bin, 
    float dist, 
    float sz, 
    int tandem
);

// routine to create volume PSF from XZ cross-section for circular confocal 
// by summing neighboring pixels for undersampled case 
void rdcircsum(
    float *vol, 
    osm_ds *head,
    int bin, 
    float dist, 
    float sz, 
    int tandem
);

//routine to create volume PSF from XZ cross-section for slit confocal 
void rotdiskline(
    float *vol, 
    osm_ds *head,
    int bin, 
    float dist, 
    float sz, 
    int tandem
);

// routine to create volume PSF from XZ cross-section for slit confocal 
// by summing neighboring pixels for undersampled case 
void rdlinesum(
    float *vol, 
    osm_ds *head,
    int bin, 
    float dist, 
    float sz, 
    int tandem
);

};
