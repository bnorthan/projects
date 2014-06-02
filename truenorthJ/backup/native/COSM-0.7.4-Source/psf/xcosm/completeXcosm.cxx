/****************************************************************************
 * Copyright (c) 2004 Einir Valdimarsson and Chrysanthe Preza
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 ****************************************************************************/

#include "xcosm.h"
#include "string.h"

extern "C" {
   extern float deltar;
   extern float deltaxy;
   extern float deltaxy_nyq;
};

using namespace blitz;

namespace cosm {

template<typename T>
void CompleteXcosm<T>::rotate(
    bool exact,
    PsfUser* user
){
    int nZ = this->radialPSF_->nZ();
    int nXY = this->radialPSF_->nXY();
    this->psf_.resize(nZ, nXY, nXY);
    Array<T,2> psf = this->radialPSF_->psf();
    radial_.resize(psf.extent());
    radial_ = cast<float>(psf);
    TinyVector<int, 3> extent(nZ, nXY, nXY);
    complete_.resize(extent);
    osm_ds head;
    memset((void*)&head,0, 1024);
    head.data = (char*)radial_.data();
    head.mode = WU_FLOAT;
    head.nx = this->radialPSF_->nR(); 
    head.ny = nZ;
    head.nz = 1;
    head.user16_footer_size = 0;
    head.user15 = TVAL; 

    head.xstart = this->radialPSF_->oversampling();
    head.ystart = nXY;
    head.zstart = this->radialPSF_->isSymmetric();
    head.ylength = this->radialPSF_->deltaXY();
    head.xlength = this->radialPSF_->deltaR();
    
    deltar = (float) this->radialPSF_->deltaR();
    deltaxy = (float) this->radialPSF_->deltaXY();
    deltaxy_nyq = (float) this->radialPSF_->deltaXYNyq();
	
	double distance = (double)distance_ == 0 || distance_ > 1e15 ? 
        1e20  :							/* single aperture use huge distance */
        distance / (lm_ * magY_ * 1E3);  /* convert to mm in object space */
		
    double fsize = (double)fsize_/(lm_ * magY_ * 1E3); /* convert to mm in object space */
    bool use2Photon =  this->type_ == CONFOCAL_ROTATING_DISK_CIRCULAR_APERTURE && fsize < deltaxy ? true : false;

    fsize = fsize/deltar < 0 ? 1 : fsize/deltar;
	distance = distance/deltar < 0 ? 1 : distance/deltar;	

    switch ( this->type_ )
    {
        case OPTICAL_SECTIONING_WIDEFIELD:
	    rotnone(complete_.data(), &head);
            break;
        case OPTICAL_SECTIONING_2_PHOTON: 
	    rotnone(complete_.data(), &head);
            complete_ = complete_ * complete_;
            break;
        case CONFOCAL_ROTATING_DISK_CIRCULAR_APERTURE:
            if ( use2Photon )
            {
			    rotnone(complete_.data(), &head);
				complete_ = complete_ * complete_;
            }
            else
            {
                rotdiskcirc(complete_.data(),&head,1,distance,fsize,0);   
            }
            break;
        case CONFOCAL_ROTATING_DISK_LINE_APERTURE: 
            rotdiskline(complete_.data(),&head,1,distance,fsize,0); 
            break;
    }
    this->psf_ = cast<T>(complete_);
    this->psf_ /= sum(this->psf_);
};

template<typename T>
void CompleteXcosm<T>::rotateAndSum(
    bool exact,
    PsfUser* user
){
    int nZ = this->radialPSF_->nZ();
    int nXY = this->radialPSF_->nXY();
    this->psf_.resize(nZ, nXY, nXY);
    Array<T,2> psf = this->radialPSF_->psf();
    radial_.resize(psf.extent());
    radial_ = cast<float>(psf);
    TinyVector<int, 3> extent(nZ, nXY, nXY);
    complete_.resize(extent);
    osm_ds head;
    memset((void*)&head,0, 1024);
    head.data = (char*)radial_.data();
    head.mode = WU_FLOAT;
    head.nx = this->radialPSF_->nR();
    head.ny = nZ;
    head.nz = 1;
    head.user16_footer_size = 0;
    head.user15 = TVAL;

    head.xstart = this->radialPSF_->oversampling();
    head.ystart = nXY;
    head.zstart = this->radialPSF_->isSymmetric();
    head.ylength = this->radialPSF_->deltaXY();
    head.xlength = this->radialPSF_->deltaR();

    deltar = (float) this->radialPSF_->deltaR();
    deltaxy = (float) this->radialPSF_->deltaXY();
    deltaxy_nyq = (float) this->radialPSF_->deltaXYNyq();

	double distance = (double)distance_ == 0 || distance_ > 1e15 ? 
        1e20  :							/* single aperture use huge distance */
        distance / (lm_ * magY_ * 1E3);  /* convert to mm in object space */
		
    double fsize = (double)fsize_/(lm_ * magY_ * 1E3); /* convert to mm in object space */
    bool use2Photon =  this->type_ == CONFOCAL_ROTATING_DISK_CIRCULAR_APERTURE && fsize < deltaxy ? true : false;

    fsize = fsize/deltar < 0 ? 1 : fsize/deltar;
	distance = distance/deltar < 0 ? 1 : distance/deltar;	

    switch ( this->type_ )
    {
        case OPTICAL_SECTIONING_WIDEFIELD:
            rotsum(complete_.data(), &head);
            break;
        case OPTICAL_SECTIONING_2_PHOTON:
            rotsum(complete_.data(), &head);
            complete_ = complete_ * complete_;
            break;
        case CONFOCAL_ROTATING_DISK_CIRCULAR_APERTURE:
            if ( use2Photon )
            {
			    rotsum(complete_.data(), &head);
				complete_ = complete_ * complete_;
            }
            else
            {
                rdcircsum(complete_.data(),&head,1,distance,fsize,0);   
            }
            break;
        case CONFOCAL_ROTATING_DISK_LINE_APERTURE:
            rdlinesum(complete_.data(),&head,1,distance,fsize,0);
            break;
    }
    this->psf_ = cast<T>(complete_);
    this->psf_ /= sum(this->psf_);
};

};
