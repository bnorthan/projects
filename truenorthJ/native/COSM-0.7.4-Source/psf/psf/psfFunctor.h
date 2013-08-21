#ifndef _PSF_FUNCTOR_H
#define _PSF_FUNCTOR_H

#include "psf/functor.h"

namespace cosm {

template<typename T>
class PsfFunctor : public Functor<T> {

  public:

    PsfFunctor() {};
    ~PsfFunctor() {};

    virtual bool isSymmetric() { return false; };

  protected:

    // not allowed
    PsfFunctor( PsfFunctor<T>& );
    PsfFunctor& operator=( PsfFunctor<T>& );

};
};

#endif // _PSF_FUNCTOR_H
