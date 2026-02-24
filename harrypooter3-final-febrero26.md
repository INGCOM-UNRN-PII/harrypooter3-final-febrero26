
# El Juez Dredd
**branch/revision:** main, 6246ddd,
## Checkstyle código
## Checkstyle Report - Dredd
**Version checkstyle:** 10.9.2
### Archivos procesados
    
#### Archivo: final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/CentroDistribucion.java
##### Archivo OK.
    
#### Archivo: final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/Envio.java
##### Archivo OK.
    
#### Archivo: final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/EstadisticasCentro.java
##### Archivo OK.
    
#### Archivo: final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/EventoEstado.java
##### Archivo OK.
    
#### Archivo: final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/GestorLogistica.java
##### Archivo OK.
    
#### Archivo: final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/GestorLogisticaBase.java
##### Archivo OK.
    
#### Archivo: final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/GestorLogisticaConsultable.java
##### Archivo OK.
    
#### Archivo: final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/GestorLogisticaOperable.java
##### Archivo OK.
    
#### Archivo: final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/Guardable.java
##### Archivo OK.
    
#### Archivo: final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/LoaderApp.java
##### Archivo OK.
    
#### Archivo: final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/Prioridad.java
##### Archivo OK.
    
## PMD código
## PMD
    
### Archivo final-febrero26-submissions/harrypooter3-final-febrero26/src/main/java/ar/unrn/LoaderApp.java
#### CyclomaticComplexity de Design en la línea 35
 - Descripción:  The method 'main(String[])' has a cyclomatic complexity of 11. 
 - Encontrado en: package: `ar.unrn`, clase: `LoaderApp` método: `main` variable: ``
 [+Información](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#cyclomaticcomplexity)
 
  
#### AvoidPrintStackTrace de Best Practices en la línea 56
 - Descripción:  Avoid printStackTrace(); use a logger call instead. 
 - Encontrado en: package: `ar.unrn`, clase: `LoaderApp` método: `main` variable: ``
 [+Información](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#avoidprintstacktrace)
 
  
#### LooseCoupling de Best Practices en la línea 89
 - Descripción:  Avoid using implementation types like 'ArrayList'; use the interface instead 
 - Encontrado en: package: `ar.unrn`, clase: `LoaderApp` método: `getClasses` variable: ``
 [+Información](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#loosecoupling)
 
  
## Conan ejecuta los Tests
### ar.unrn.CentroEstadisticasTest
Total de tests: 4. De los cuales, fallos en: 0, errores en: 0
- estadisticasPesoCero() (en 0.065 segundos): Pasó
- estadisticasPorcentajeCero() (en 0.002 segundos): Pasó
- estadisticasPorcentajeEntregado() (en 0.007 segundos): Pasó
- estadisticasPesoTotal() (en 0.005 segundos): Pasó


### ar.unrn.CentroListadosTest
Total de tests: 5. De los cuales, fallos en: 0, errores en: 0
- listarEnviosPrioridad() (en 0.005 segundos): Pasó
- envioNoExiste() (en 0.009 segundos): Pasó
- listarEnviosPorPeso() (en 0.003 segundos): Pasó
- envioRegistrado() (en 0.001 segundos): Pasó
- buscarEnvios() (en 0.001 segundos): Pasó


### ar.unrn.CentroOperacionesTest
Total de tests: 6. De los cuales, fallos en: 0, errores en: 0
- vaciarEnvios() (en 0.001 segundos): Pasó
- eliminarEnvio() (en 0.002 segundos): Pasó
- registrarEnvioDuplicado() (en 0.001 segundos): Pasó
- registrarEnvio() (en 0.001 segundos): Pasó
- agregarEventoAlHistorial() (en 0.001 segundos): Pasó
- ubicacionCiudad() (en 0.002 segundos): Pasó


### ar.unrn.EnvioComportamientoTest
Total de tests: 6. De los cuales, fallos en: 0, errores en: 0
- mismaInstancia() (en 0.002 segundos): Pasó
- hashCodeIguales() (en 0.006 segundos): Pasó
- trackingDistinto() (en 0.006 segundos): Pasó
- historialEvento() (en 0.002 segundos): Pasó
- entregadoFalse() (en 0.005 segundos): Pasó
- distintoDeNull() (en 0.003 segundos): Pasó


### ar.unrn.EnvioEntregaTest
Total de tests: 2. De los cuales, fallos en: 0, errores en: 0
- eventoEntregado() (en 0.002 segundos): Pasó
- historialInmodificable() (en 0.002 segundos): Pasó


### ar.unrn.EnvioTest
Total de tests: 7. De los cuales, fallos en: 0, errores en: 0
- destinatarioNull() (en 0.004 segundos): Pasó
- prioridadNull() (en 0.004 segundos): Pasó
- pesoCero() (en 0.004 segundos): Pasó
- destinatarioEnVacio() (en 0.003 segundos): Pasó
- pesoNegativo() (en 0.001 segundos): Pasó
- trackingIdEnVacio() (en 0.002 segundos): Pasó
- trackingIdNull() (en 0.001 segundos): Pasó


### ar.unrn.EstadisticasCentroTest
Total de tests: 8. De los cuales, fallos en: 0, errores en: 0
- porcentajeMayorCien() (en 0.001 segundos): Pasó
- equalsFalseContraNull() (en 0.001 segundos): Pasó
- noPermitePorcentajeNegativo() (en 0.001 segundos): Pasó
- equalsMismaInstancia() (en 0.001 segundos): Pasó
- hashCodeValoresIguales() (en 0.001 segundos): Pasó
- pesoTotalValor() (en 0.001 segundos): Pasó
- porcentajeValor() (en 0.001 segundos): Pasó
- noPermitePesoNegativo() (en 0.001 segundos): Pasó


### ar.unrn.EventoEstadoTest
Total de tests: 8. De los cuales, fallos en: 0, errores en: 0
- equalsFalseNull() (en 0.001 segundos): Pasó
- equalsMismaInstancia() (en 0.001 segundos): Pasó
- equalsOtroTipo() (en 0.001 segundos): Pasó
- fechaHoraNull() (en 0.002 segundos): Pasó
- fechaHoraConstructor() (en 0.001 segundos): Pasó
- descripcionEnVacio() (en 0.002 segundos): Pasó
- hashCodeEventosIguales() (en 0.001 segundos): Pasó
- equalsEventosIguales() (en 0.001 segundos): Pasó


### ar.unrn.GestorConsultasEstadisticasSinEnviosTest
Total de tests: 2. De los cuales, fallos en: 0, errores en: 0
- pesoTotalCero() (en 0.026 segundos): Pasó
- porcentajeCero() (en 0.002 segundos): Pasó


### ar.unrn.GestorConsultasEstadisticasTest
Total de tests: 2. De los cuales, fallos en: 0, errores en: 0
- porcentajeEntregado() (en 0.009 segundos): Pasó
- pesoTotal() (en 0.001 segundos): Pasó


### ar.unrn.GestorConsultasOrdenTest
Total de tests: 2. De los cuales, fallos en: 0, errores en: 0
- listarPesoDescendiente() (en 0.005 segundos): Pasó
- listarPrioridadDescendiente() (en 0.006 segundos): Pasó


### ar.unrn.GestorConsultasTest
Total de tests: 3. De los cuales, fallos en: 0, errores en: 0
- buscarDestinatario() (en 0.002 segundos): Pasó
- estadoEntregado() (en 0.004 segundos): Pasó
- buscarTracking() (en 0.001 segundos): Pasó


### ar.unrn.GestorLogisticaBaseTest
Total de tests: 6. De los cuales, fallos en: 0, errores en: 0
- exigirTrackingId() (en 0.002 segundos): Pasó
- envioMensajeEstandar() (en 0.001 segundos): Pasó
- centroNoExistente() (en 0.002 segundos): Pasó
- encontrarCentro() (en 0.002 segundos): Pasó
- exigirTexto() (en 0.001 segundos): Pasó
- exigirNoNulo() (en 0.001 segundos): Pasó


### ar.unrn.GestorOperacionesTest
Total de tests: 5. De los cuales, fallos en: 0, errores en: 0
- vaciarCentro() (en 0.001 segundos): Pasó
- eliminarEnvio() (en 0.008 segundos): Pasó
- centroDuplicado() (en 0.002 segundos): Pasó
- crearCentro() (en 0.001 segundos): Pasó
- trackingDuplicado() (en 0.001 segundos): Pasó


### ar.unrn.GestorPersistenciaTest
Total de tests: 4. De los cuales, fallos en: 0, errores en: 0
- cargarNoGestor() (en 0.023 segundos): Pasó
- recuperarEnvio() (en 0.045 segundos): Pasó
- guardarRutaVacio() (en 0.002 segundos): Pasó
- cargarRutaVacio() (en 0.002 segundos): Pasó


