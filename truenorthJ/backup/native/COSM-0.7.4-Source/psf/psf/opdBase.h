/****************************************************************************
 * Copyright (c) 2007 Einir Valdimarsson and Chrysanthe Preza
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

#ifndef _OPD_BASE_H
#define _OPD_BASE_H

#include "functor.h"
#include <math.h>

namespace cosm {

// Base class to compute the OPD for given optical parameters and model
template<typename T>
class opdBase : public Functor<T> {

  public:

    // constructor
    opdBase(
        T ts, 		// specimen thickness
        T tid,		// immersion thickness design
        T tia,		// immersion thickness actual
        T tgd,		// coverglass thickness design
        T tga,		// coverglass thickness actual
        T ns,		// specimen refractive index 
        T nid,		// immersion refractive index design
        T nia,		// immersion refractive index actual
        T ngd,		// coverglass refractive index design
        T nga		// coverglass refractive index actual
    ) : ts_(ts), tid_(tid), tia_(tia), tgd_(tgd), tga_(tga), 
	ns_(ns), nid_(nid), nia_(nia), ngd_(ngd), nga_(nga), 
	nssq_(ns*ns), 
	nidsq_(nid*nid), niasq_(nia*nia), 
	ngdsq_(ngd*ngd), ngasq_(nga*nga), 
	z_(0),
	symmetric_((ngd == nga) && (tgd == tga) && (nid == nia) && (ts == 0.0))
    {};

    // destructor
    virtual ~opdBase() {};

    // function to evaluate the OPD
    virtual T operator()( T rhoNAsq ) = 0; 

    virtual T amplitude( T rhoNAsq ) { return 1.0; }

    // function to set the z variable
    void setZ(T z) { z_ = z; };

    // abberation is symmetric
    bool isSymmetric() { return symmetric_; };
 
    T ns() { return ns_; };
    T nga() { return nga_; };
    T nia() { return nia_; };

  protected:

    // function to compute the common term:  t*sqrt(1-(NA*rho/n)**2)
    T term(
	T t,
	T nsq,
	T rhoNAsq
    ) { return t * sqrt( nsq - rhoNAsq); };

    // function to compute the common term:  sqrt(1-(NA*rho/n)**2)
    T term1(
	T nsq,
	T rhoNAsq
    ) { return sqrt( nsq - rhoNAsq); };

  protected:

    // not allowed
    opdBase(opdBase<T>&);
    opdBase& operator=(opdBase<T>&);

  protected:

    T ts_; 	// specimen thickness actual
    T tid_;	// immersion thickness actual
    T tia_;	// immersion thickness design
    T tgd_;	// coverglass thickness actual
    T tga_;	// coverglass thickness design

    T ns_;	// specimen refractive index actual
    T nid_;	// immersion refractive index actual
    T nia_;	// immersion refractive index design
    T ngd_;	// coverglass refractive index actual
    T nga_;	// coverglass refractive index design

    T nssq_;	// ns*ns
    T nidsq_;   // nid*nid
    T niasq_;	// nia*nia
    T ngdsq_;   // ngd*ngd
    T ngasq_;   // nga*nga

    T z_;	// variable in Z axis
    bool symmetric_;

};

}

#endif // _OPD_BASE_H
