/*
***************************************************************
*                                                             *
*                           NOTICE                            *
*                                                             *
*   THIS SOFTWARE IS THE PROPERTY OF AND CONTAINS             *
*   CONFIDENTIAL INFORMATION OF INFOR AND/OR ITS AFFILIATES   *
*   OR SUBSIDIARIES AND SHALL NOT BE DISCLOSED WITHOUT PRIOR  *
*   WRITTEN PERMISSION. LICENSED CUSTOMERS MAY COPY AND       *
*   ADAPT THIS SOFTWARE FOR THEIR OWN USE IN ACCORDANCE WITH  *
*   THE TERMS OF THEIR SOFTWARE LICENSE AGREEMENT.            *
*   ALL OTHER RIGHTS RESERVED.                                *
*                                                             *
*   (c) COPYRIGHT 2013 INFOR.  ALL RIGHTS RESERVED.           *
*   THE WORD AND DESIGN MARKS SET FORTH HEREIN ARE            *
*   TRADEMARKS AND/OR REGISTERED TRADEMARKS OF INFOR          *
*   AND/OR ITS AFFILIATES AND SUBSIDIARIES. ALL RIGHTS        *
*   RESERVED.  ALL OTHER TRADEMARKS LISTED HEREIN ARE         *
*   THE PROPERTY OF THEIR RESPECTIVE OWNERS.                  *
*                                                             *
***************************************************************
*/
package mvx.app.pgm;

import mvx.app.common.*;
import mvx.app.util.*;
import mvx.app.plist.*;
import mvx.app.ds.*;
import mvx.util.*;

/*
*Modification area - M3
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Business partner
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Customer
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*/

/**
*<BR><B><FONT SIZE=+2>Lst: Supplier Invoice Batch - Select Operation</FONT></B><BR><BR>
*
* <PRE>
* Function program APS455Fnc is called by interactive program APS455 to
* mangage batch selections and operations for supplier invoices.
*
* The following interactions and operations are available:
*
* Interaction maintain
* --------------------
* parameter list: cPXAPS455FncINmaintain (instantiated as pMaintain )
* Interaction used when adding, changing, retrieving or submitting a 
* selection of supplier invoices.
* Returns any errors or notifications.
*
* Interaction delete
* ------------------
* parameter list: cPXAPS455FncINdelete (instantiated as pDelete )
* Interaction used when deleting a supplier invoice.
* Returns any errors or notifications.
*
* Interaction copy
* ----------------
* parameter list: cPXAPS455FncINcopy (instantiated as pCopy )
* Interaction used when copying a supplier invoice.
* Returns any errors or notifications.
*
* Interaction settings
* --------------------
* parameter list: cPXAPS455FncINsettings (instantiated as pSettings )
* Interaction used when setting parameters for APS455 function.
* Returns any errors or notifications.
*
* Returns notification 'XRE0103 - Record does not exist' in step INITIATE if there 
* is no settings record for the responsible. In that case default values are 
* returned for the parameters and a record will be written in step UDPATE.
* </PRE>
*/
public class APS455Fnc extends Function
{
   public void movexMain() {
      INIT(); 	
      // Interaction maintain
      // =========================================
      if (pMaintain != null) {
         if (pMaintain.getStep() == cEnumStep.INITIATE) {
            do_maintain_initiate();
         } else if (pMaintain.getStep() == cEnumStep.VALIDATE) {
            do_maintain_validate();
         } else if (pMaintain.getStep() == cEnumStep.UPDATE) {
            do_maintain_update();
         }
      // Interaction delete
      // =========================================
      } else if (pDelete != null) {
         if (pDelete.getStep() == cEnumStep.INITIATE) {
            do_delete_initiate();
         } else if (pDelete.getStep() == cEnumStep.VALIDATE) {
            do_delete_validate();
         } else if (pDelete.getStep() == cEnumStep.UPDATE) {
            do_delete_update();
         }
      // Interaction copy
      // =========================================
      } else if (pCopy != null) {
         if (pCopy.getStep() == cEnumStep.INITIATE) {
            do_copy_initiate();
         } else if (pCopy.getStep() == cEnumStep.VALIDATE) {
            do_copy_validate();
         } else if (pCopy.getStep() == cEnumStep.UPDATE) {
            do_copy_update();
         }
      // Interaction settings
      // =========================================
      } else if (pSettings != null) {
         if (pSettings.getStep() == cEnumStep.INITIATE) {
            do_settings_initiate();
         } else if (pSettings.getStep() == cEnumStep.VALIDATE) {
            do_settings_validate();
         } else if (pSettings.getStep() == cEnumStep.UPDATE) {
            do_settings_update();
         }
      } else {
         pListError();
      }
      SETLR();
      return;
   }

   /**
   * Interaction maintain - step INITIATE.
   */
   public void do_maintain_initiate() {
      // Declaration
      boolean found_CSYSTP = false;
      // Get record
      // =========================================
      if (pMaintain.passCSYSTP && pMaintain.getMode() != cEnumMode.ADD) {
         // Move record from calling program
         moveCSYSTP(SYSTP, pMaintain.SYSTP);
         // Set primary keys
         pMaintain.RESP.set().moveLeftPad(SYSTP.getRESP());
         pMaintain.LIVR.set().moveLeftPad(SYSTP.getLIVR());
      } else {
         // Validate primary key parameters
         // =========================================
         // Responsible
         pMaintain.RESP.validateMANDATORYandConstraints();
         // Report version
         pMaintain.LIVR.validateMANDATORYandConstraints();
         if (!pMaintain.RESP.isBlank() && pMaintain.LIVR.get().NE(pMaintain.RESP.get())) {
            // MSGID=WLI1101 Report version &1 is invalid
            pMaintain.messages.addError(this.DSPGM, "LIVR", "WLI1101", pMaintain.LIVR.get());
         }
         // Return error messages
         if (pMaintain.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pMaintain.parameters.setAllDISABLEDAccess();
            pMaintain.LIVR.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         SYSTP.setCONO(currentCONO);
         SYSTP.setDIVI().move(LDAZD.DIVI);
         SYSTP.setRESP().move(pMaintain.RESP.get());
         SYSTP.setLIVR().move(pMaintain.LIVR.get());
         found_CSYSTP = SYSTP.CHAIN("00", SYSTP.getKey("00"));
         if (pMaintain.getMode() == cEnumMode.ADD) {
            if (found_CSYSTP) {
               // MSGID=WLI1104 Report version &1 already exist
               pMaintain.messages.addError(this.DSPGM, "LIVR", "WLI1104", SYSTP.getLIVR());
            } else {
               // Set default values
               SYSTP.clearNOKEY("00");
               maintain_initiate_setDefaults();
               SYSTP.setPARA().moveLeftPad(APS455DS.get());
            }
         } else if (!found_CSYSTP) {
            if (pMaintain.noUpdate.get()) {
               // Set default values
               SYSTP.clearNOKEY("00");
               maintain_initiate_setDefaults();
               SYSTP.setPARA().moveLeftPad(APS455DS.get());
            } else {
               // MSGID=WLI1103 Report version &1 does not exist
               pMaintain.messages.addError(this.DSPGM, "LIVR", "WLI1103", SYSTP.getLIVR());
            }
         }
         // Return error messages
         if (pMaintain.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pMaintain.parameters.setAllDISABLEDAccess();
            pMaintain.LIVR.setOUTAccess();
            return;
         }
      }
      // Set parameters
      // =========================================
      maintain_initiate_setParameters();
      // Return error messages
      if (pMaintain.messages.existError()) {
         // Set key/optional/mandatory parameters to access mode OUT
         // and other parameters to DISABLED.
         pMaintain.parameters.setAllDISABLEDAccess();
         pMaintain.LIVR.setOUTAccess();
         if (CRCommon.isCentralUser()) {
            pMaintain.selectDIVI.setOUTAccess();
         }
         pMaintain.fromINBN.setOUTAccess();
         pMaintain.IBOP.setOUTAccess();
         return;
      }
      // Set Access mode
      // =========================================
      if (pMaintain.getMode() == cEnumMode.RETRIEVE) {
         maintain_setAccessModeForRetrieve();
      } else {
         maintain_setAccessModeForEntry();
      }
      // Move updated record back to calling program
      // =========================================
      moveCSYSTP(pMaintain.SYSTP, SYSTP);
   }

   /**
   * Sets default values for fields in ADD mode step INITIATE of interaction maintain.
   * This method is to ensure that all fields have valid values after clearNOKEY has 
   * been performed.
   */
   public void maintain_initiate_setDefaults() {
      APS455DS.set().clear();
      // Division
      APS455DS.setZWDIVI().moveLeftPad(LDAZD.DIVI);  
      // Invoice batch operation
      APS455DS.setZWIBOP(cRefIBOPext.VALIDATE());
      // Report layout
      APS455DS.setZWLITP(1); // Invoice
   }

   /**
   * Sets parameters in step INITIATE of interaction maintain.
   */
   public void maintain_initiate_setParameters() {
      APS455DS.set().moveLeftPad(SYSTP.getPARA());
      // Division
      if (CRCommon.isCentralUser()) {
         if (pMaintain.selectDIVI.isBlank()) {
            pMaintain.selectDIVI.set().moveLeftPad(APS455DS.getZWDIVI());
         } else {
            pMaintain.selectDIVI.validateMANDATORYandConstraints();
         }
      } else {
         pMaintain.selectDIVI.set().moveLeftPad(LDAZD.DIVI);
      }
      // Invoice batch operation
      if (pMaintain.IBOP.isBlank()) {
         pMaintain.IBOP.set(APS455DS.getZWIBOP());
      } else {
         pMaintain.IBOP.validateMANDATORYandConstraints();
         pMaintain.work_IBOPpreset = true;
      }
      pMaintain.work_lastIBOP = pMaintain.IBOP.get();
      // Invoice batch number
      if (pMaintain.fromINBN.isBlank()) {
         pMaintain.fromINBN.set(APS455DS.getZFINBN());
         pMaintain.toINBN.set(APS455DS.getZTINBN());
      } else {
         pMaintain.work_INBNpreset = true;
         pMaintain.fromINBN.validateMANDATORYandConstraints();
         pMaintain.toINBN.set(pMaintain.fromINBN.get());
         // Also display supplier invoice number
         found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, found_FAPIBH, currentCONO, pMaintain.selectDIVI.get(), pMaintain.fromINBN.get());
         if (found_FAPIBH) {
            pMaintain.fromSINO.set().moveLeftPad(APIBH.getSINO());
            pMaintain.toSINO.set().moveLeftPad(APIBH.getSINO());
         }
      }
      // Only INBN and SINO selectable for operation PRINT
      if (pMaintain.IBOP.get() == cRefIBOPext.PRINT()) {
         // Invoice batch number
         pMaintain.toINBN.clearValue();
         // Supplier invoice no
         if (!pMaintain.work_INBNpreset) {
            pMaintain.fromSINO.set().moveLeftPad(APS455DS.getZFSINO());
         }
         pMaintain.toSINO.clearValue();
      }
      // If INBN is preset or for operation PRINT, then other selection parameters should be blank
      if (!pMaintain.work_INBNpreset && pMaintain.IBOP.get() != cRefIBOPext.PRINT()) {
         // Supplier invoice no
         pMaintain.fromSINO.set().moveLeftPad(APS455DS.getZFSINO());
         pMaintain.toSINO.set().moveLeftPad(APS455DS.getZTSINO());
         // Invoice batch type
         pMaintain.fromIBTP.set().moveLeftPad(APS455DS.getZFIBTP());
         pMaintain.toIBTP.set().moveLeftPad(APS455DS.getZTIBTP());
         // Invoice status
         pMaintain.fromSUPA.set(APS455DS.getZFSUPA());
         pMaintain.toSUPA.set(APS455DS.getZTSUPA());
         // Supplier
         pMaintain.fromSUNO.set().moveLeftPad(APS455DS.getZFSUNO());
         pMaintain.toSUNO.set().moveLeftPad(APS455DS.getZTSUNO());
         // Invoice date
         pMaintain.fromIVDT.set(APS455DS.getZFIVDT());
         pMaintain.toIVDT.set(APS455DS.getZTIVDT());
         // Authorized user
         pMaintain.fromAPCD.set().moveLeftPad(APS455DS.getZFAPCD());
         pMaintain.toAPCD.set().moveLeftPad(APS455DS.getZTAPCD());
         // Invoice batch head errors
         pMaintain.fromIBHE.set(APS455DS.getZFIBHE());
         pMaintain.toIBHE.set(APS455DS.getZTIBHE());
         // Invoice batch line errors
         pMaintain.fromIBLE.set(APS455DS.getZFIBLE());
         pMaintain.toIBLE.set(APS455DS.getZTIBLE());
      }
      // Report layout
      pMaintain.LITP.set(APS455DS.getZWLITP());
      // Accounting parameters
      if (pMaintain.IBOP.get() == cRefIBOPext.UPDATE_TO_APL()) {
         // Accounting date
         if (pMaintain.ACDT.isBlank()) {
            pMaintain.ACDT.set(APS455DS.getZWACDT());
         }
         // Voucher text
         pMaintain.VTXT.set().moveLeftPad(APS455DS.getZWVTXT());
         //if (pMaintain.VTXT.get().isBlank() &&
         //    pMaintain.work_INBNpreset) {
         //   // Voucher text can only be proposed if only one invoice
         //   XQSUNO.move(APIBH.getSUNO());
         //   XXSINO.moveLeft(APIBH.getSINO());
         //   CRCalendar.lookUpDate_blankDIVI(currentCONO, APIBH.getIVDT());      
         //   XXINYR.move(CRCalendar.getYear());
         //   found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, false, currentCONO, APIBH.getSPYN());
         //   XXALSU.move(IDMAS.getALSU());
         //   pMaintain.VTXT.set().moveLeftPad(XXVTXT);
         //}
      }
      // Entry date
      pMaintain.RGDT.set(SYSTP.getRGDT());
      // Change date
      pMaintain.LMDT.set(SYSTP.getLMDT());
      // Change ID
      pMaintain.CHID.set().moveLeftPad(SYSTP.getCHID());
      // Change number
      pMaintain.work_CHNO = SYSTP.getCHNO();
   }

   /**
   * Interaction maintain - step VALIDATE.
   */
   public void do_maintain_validate() {
      // Declaration
      boolean updateToAPLactivated = false;
      boolean updateToAPLdeactivated = false;
      boolean printActivated = false;
      boolean printDeactivated = false;
      boolean IBOPchanged = false;
      // Validate parameters and build query selection
      // =========================================
      // Invoice batch operation
      pMaintain.IBOP.validateMANDATORYandConstraints();
      IBOPchanged = pMaintain.IBOP.get() != pMaintain.work_lastIBOP;
      if (IBOPchanged) {
         // Make sure the panel is displayed again
         pMaintain.setNewEntryContext();
      }
      updateToAPLactivated = 
         pMaintain.IBOP.get() == cRefIBOPext.UPDATE_TO_APL() &&
         pMaintain.work_lastIBOP != cRefIBOPext.UPDATE_TO_APL();
      updateToAPLdeactivated =
         pMaintain.work_lastIBOP == cRefIBOPext.UPDATE_TO_APL() &&
         pMaintain.IBOP.get() != cRefIBOPext.UPDATE_TO_APL();
      printActivated = 
         pMaintain.IBOP.get() == cRefIBOPext.PRINT() &&
         pMaintain.work_lastIBOP != cRefIBOPext.PRINT();
      printDeactivated =
         pMaintain.work_lastIBOP == cRefIBOPext.PRINT() &&
         pMaintain.IBOP.get() != cRefIBOPext.PRINT();
      pMaintain.work_lastIBOP = pMaintain.IBOP.get();
      if (updateToAPLactivated) {
         pMaintain.ACDT.setMANDATORYAccess();
         pMaintain.VTXT.setMANDATORYAccess();
         pMaintain.ACDT.set(movexDate());
         pMaintain.VTXT.clearValue();
      }
      if (updateToAPLdeactivated) {
         pMaintain.ACDT.setDISABLEDAccess();
         pMaintain.VTXT.setDISABLEDAccess();
         pMaintain.ACDT.clearValue();
         pMaintain.VTXT.clearValue();
      }
      if (updateToAPLactivated || updateToAPLdeactivated) {
         // Make sure the panel is displayed again
         pMaintain.setNewEntryContext();
      }
      if (printActivated) {
         // Clear selection parameters that are not used
         // Supplier invoice no
         pMaintain.toSINO.clearValue();
         // Invoice batch number
         pMaintain.toINBN.clearValue();
         // Invoice batch type
         pMaintain.fromIBTP.clearValue();
         pMaintain.toIBTP.clearValue();
         // Invoice status
         pMaintain.fromSUPA.clearValue();
         pMaintain.toSUPA.clearValue();
         // Supplier
         pMaintain.fromSUNO.clearValue();
         pMaintain.toSUNO.clearValue();
         // Invoice date
         pMaintain.fromIVDT.clearValue();
         pMaintain.toIVDT.clearValue();
         // Authorized user
         pMaintain.fromAPCD.clearValue();
         pMaintain.toAPCD.clearValue();
         // Invoice batch head errors
         pMaintain.fromIBHE.clearValue();
         pMaintain.toIBHE.clearValue();
         // Invoice batch line errors
         pMaintain.fromIBLE.clearValue();
         pMaintain.toIBLE.clearValue();
      }
      if (printActivated || printDeactivated) {
         // Refresh access modes
         maintain_setAccessModeForEntry();
         // Make sure the panel is displayed again
         pMaintain.setNewEntryContext();
         return;
      }
      // Company
      this.PXCONO = currentCONO;
      this.PXDIVI.clear();
      this.MSGID.moveLeftPad(cRefCONO.heading().getID());
      this.MSGID.move("00");
      PXQRYSLT.PXTYP.move("N ");
      PXQRYSLT.PXFLD.moveLeftPad("E5CONO");
      PXQRYSLT.PXFNU = (double)currentCONO;
      PXQRYSLT.PXTNU = (double)currentCONO;
      PXQRYSLT.PXORD = -1;
      PXQRYSLT.PXBRK = 0;
      PXQRYSLT.PXSUM = 0;
      PXQRYSLT.CQRYSLT();
      // Division
      this.PXCONO = currentCONO;
      this.PXDIVI.clear();
      this.MSGID.moveLeftPad(cRefDIVI.heading().getID());
      this.MSGID.move("00");
      PXQRYSLT.PXTYP.move("A ");
      PXQRYSLT.PXFLD.moveLeftPad("E5DIVI");
      if (CRCommon.isCentralUser()) {
         pMaintain.selectDIVI.validateMANDATORYandConstraints();
         found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
         found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.selectDIVI.get());
         if (pMaintain.selectDIVI.validateExists(found_CMNDIV)) {
            // Check access authority for division
            PLCHKAD.ADCONO = currentCONO; 	 	
            PLCHKAD.ADCMTP = MNCMP.getCMTP();
            PLCHKAD.ADFDIV.move(pMaintain.selectDIVI.get()); 	 	
            PLCHKAD.ADTDIV.clear();
            PLCHKAD.ADRESP.move(this.DSUSS); 	 	
            PLCHKAD.CCHKACD(); 	 	
            if (PLCHKAD.ADAERR == 1) { 	 	
               pMaintain.messages.addError(this.DSPGM, "selectDIVI", PLCHKAD.ADMSGI.toString(), PLCHKAD.ADMSGD);
            } 	 	
         }
      }
      PXQRYSLT.PXFAL.moveLeft(pMaintain.selectDIVI.get());
      PXQRYSLT.PXTAL.moveLeft(pMaintain.selectDIVI.get());
      PXQRYSLT.PXORD = 1;
      PXQRYSLT.PXBRK = 0;
      PXQRYSLT.PXSUM = 0;
      PXQRYSLT.CQRYSLT();
      // Supplier invoice no
      if (!pMaintain.work_INBNpreset) {
         if (pMaintain.fromSINO.validateMANDATORYandConstraints() &&
             pMaintain.toSINO.validateMANDATORYandConstraints()) 
         {
            if (!pMaintain.fromSINO.isBlank() || !pMaintain.toSINO.isBlank()) {
               if (pMaintain.IBOP.get() == cRefIBOPext.PRINT()) {
                  pMaintain.toSINO.set().moveLeftPad(pMaintain.toSINO.get());
               }
               this.PXCONO = currentCONO;
               this.PXDIVI.clear();
               this.MSGID.moveLeftPad(cRefSINO.heading().getID());
               this.MSGID.move("00");
               PXQRYSLT.PXTYP.move("A ");
               PXQRYSLT.PXFLD.moveLeftPad("E5SINO");
               PXQRYSLT.PXFAL.moveLeft(pMaintain.fromSINO.get());
               PXQRYSLT.PXTAL.moveLeft(pMaintain.toSINO.get());
               PXQRYSLT.PXORD = 0;
               PXQRYSLT.PXBRK = 0;
               PXQRYSLT.PXSUM = 0;
               PXQRYSLT.CQRYSLT();
               if (PXQRYSLT.PXQER != ' ') {
                  if (PXQRYSLT.PXFNO == 'T') {
                     pMaintain.messages.addError(this.DSPGM, "toSINO", this.MSGID.toString(), PXQRYSLT.PXMDA);
                  } else {
                     pMaintain.messages.addError(this.DSPGM, "fromSINO", this.MSGID.toString(), PXQRYSLT.PXMDA);
                  }
               }
            }
         }
      }
      // Invoice batch number
      if (pMaintain.fromINBN.validateMANDATORYandConstraints() &&
          pMaintain.toINBN.validateMANDATORYandConstraints()) 
      {
         if (pMaintain.IBOP.get() == cRefIBOPext.PRINT()) {
            pMaintain.toINBN.set(pMaintain.fromINBN.get());
         }
         this.PXCONO = currentCONO;
         this.PXDIVI.clear();
         this.MSGID.moveLeftPad(cRefINBN.heading().getID());
         this.MSGID.move("00");
         PXQRYSLT.PXTYP.move("N ");
         PXQRYSLT.PXFLD.moveLeftPad("E5INBN");
         PXQRYSLT.PXFNU = (double)pMaintain.fromINBN.get();
         PXQRYSLT.PXTNU = (double)pMaintain.toINBN.get();
         PXQRYSLT.PXORD = 2;
         PXQRYSLT.PXBRK = 0;
         PXQRYSLT.PXSUM = 0;
         PXQRYSLT.CQRYSLT();
         if (PXQRYSLT.PXQER != ' ') {
            if (PXQRYSLT.PXFNO == 'T') {
               pMaintain.messages.addError(this.DSPGM, "toINBN", this.MSGID.toString(), PXQRYSLT.PXMDA);
            } else {
               pMaintain.messages.addError(this.DSPGM, "fromINBN", this.MSGID.toString(), PXQRYSLT.PXMDA);
            }
         }
      }
      // - An invoice must be entered for operation PRINT
      if (pMaintain.IBOP.get() == cRefIBOPext.PRINT()) {
         if (pMaintain.fromINBN.isBlank() && pMaintain.fromSINO.isBlank()) {
            // MSGID=WINBN02 Invoice batch number must be entered
            pMaintain.messages.addError(this.DSPGM, "fromINBN", "WINBN02");
         }
      }
      // Invoice batch type
      if (pMaintain.fromIBTP.validateMANDATORYandConstraints() &&
          pMaintain.toIBTP.validateMANDATORYandConstraints()) 
      {
         if (!pMaintain.fromIBTP.isBlank() || !pMaintain.toIBTP.isBlank()) {
            this.PXCONO = currentCONO;
            this.PXDIVI.clear();
            this.MSGID.moveLeftPad(cRefIBTP.heading().getID());
            this.MSGID.move("00");
            PXQRYSLT.PXTYP.move("A ");
            PXQRYSLT.PXFLD.moveLeftPad("E5IBTP");
            PXQRYSLT.PXFAL.moveLeft(pMaintain.fromIBTP.get());
            PXQRYSLT.PXTAL.moveLeft(pMaintain.toIBTP.get());
            PXQRYSLT.PXORD = 0;
            PXQRYSLT.PXBRK = 0;
            PXQRYSLT.PXSUM = 0;
            PXQRYSLT.CQRYSLT();
            if (PXQRYSLT.PXQER != ' ') {
               if (PXQRYSLT.PXFNO == 'T') {
                  pMaintain.messages.addError(this.DSPGM, "toIBTP", this.MSGID.toString(), PXQRYSLT.PXMDA);
               } else {
                  pMaintain.messages.addError(this.DSPGM, "fromIBTP", this.MSGID.toString(), PXQRYSLT.PXMDA);
               }
            }
         }
      }
      // Invoice status
      if (pMaintain.fromSUPA.validateMANDATORYandConstraints() &&
          pMaintain.toSUPA.validateMANDATORYandConstraints()) 
      {
         if (!pMaintain.fromSUPA.isBlank() || !pMaintain.toSUPA.isBlank()) {
            this.PXCONO = currentCONO;
            this.PXDIVI.clear();
            this.MSGID.moveLeftPad(cRefSUPA.heading().getID());
            this.MSGID.move("00");
            PXQRYSLT.PXTYP.move("N ");
            PXQRYSLT.PXFLD.moveLeftPad("E5SUPA");
            PXQRYSLT.PXFNU = (double)pMaintain.fromSUPA.get();
            PXQRYSLT.PXTNU = (double)pMaintain.toSUPA.get();
            PXQRYSLT.PXORD = 0;
            PXQRYSLT.PXBRK = 0;
            PXQRYSLT.PXSUM = 0;
            PXQRYSLT.CQRYSLT();
            if (PXQRYSLT.PXQER != ' ') {
               if (PXQRYSLT.PXFNO == 'T') {
                  pMaintain.messages.addError(this.DSPGM, "toSUPA", this.MSGID.toString(), PXQRYSLT.PXMDA);
               } else {
                  pMaintain.messages.addError(this.DSPGM, "fromSUPA", this.MSGID.toString(), PXQRYSLT.PXMDA);
               }
            }
         }
      }
      // Supplier
      if (pMaintain.fromSUNO.validateMANDATORYandConstraints() &&
          pMaintain.toSUNO.validateMANDATORYandConstraints()) 
      {
         if (!pMaintain.fromSUNO.isBlank() || !pMaintain.toSUNO.isBlank()) {
            this.PXCONO = currentCONO;
            this.PXDIVI.clear();
            this.MSGID.moveLeftPad(cRefSUNO.heading().getID());
            this.MSGID.move("00");
            PXQRYSLT.PXTYP.move("A ");
            PXQRYSLT.PXFLD.moveLeftPad("E5SUNO");
            PXQRYSLT.PXFAL.moveLeft(pMaintain.fromSUNO.get());
            PXQRYSLT.PXTAL.moveLeft(pMaintain.toSUNO.get());
            PXQRYSLT.PXORD = 0;
            PXQRYSLT.PXBRK = 0;
            PXQRYSLT.PXSUM = 0;
            PXQRYSLT.CQRYSLT();
            if (PXQRYSLT.PXQER != ' ') {
               if (PXQRYSLT.PXFNO == 'T') {
                  pMaintain.messages.addError(this.DSPGM, "toSUNO", this.MSGID.toString(), PXQRYSLT.PXMDA);
               } else {
                  pMaintain.messages.addError(this.DSPGM, "fromSUNO", this.MSGID.toString(), PXQRYSLT.PXMDA);
               }
            }
         }
      }
      // Invoice date
      if (pMaintain.fromIVDT.validateMANDATORYandConstraints() &&
          pMaintain.toIVDT.validateMANDATORYandConstraints()) 
      {
         if (!pMaintain.fromIVDT.isBlank() || !pMaintain.toIVDT.isBlank()) {
            this.PXCONO = currentCONO;
            this.PXDIVI.clear();
            this.MSGID.moveLeftPad(cRefIVDT.heading().getID());
            this.MSGID.move("00");
            PXQRYSLT.PXTYP.move("D ");
            PXQRYSLT.PXFLD.moveLeftPad("E5IVDT");
            PXQRYSLT.PXFNU = (double)pMaintain.fromIVDT.get();
            PXQRYSLT.PXTNU = (double)pMaintain.toIVDT.get();
            PXQRYSLT.PXORD = 0;
            PXQRYSLT.PXBRK = 0;
            PXQRYSLT.PXSUM = 0;
            this.PXDFMI.moveLeftPad("YMD8");
            this.PXDFMO.moveLeftPad("YMD8");
            PXQRYSLT.CQRYSLT();
            if (PXQRYSLT.PXQER != ' ') {
               if (PXQRYSLT.PXFNO == 'T') {
                  pMaintain.messages.addError(this.DSPGM, "toIVDT", this.MSGID.toString(), 
                     CRCommon.formatDateForMsg(pMaintain.toIVDT.get(), pMaintain.getDTFM(), pMaintain.getDSEP()));
               } else {
                  pMaintain.messages.addError(this.DSPGM, "fromIVDT", this.MSGID.toString(), 
                     CRCommon.formatDateForMsg(pMaintain.fromIVDT.get(), pMaintain.getDTFM(), pMaintain.getDSEP()));
               }
            }
         }
      }
      // Authorized user
      if (pMaintain.fromAPCD.validateMANDATORYandConstraints() &&
          pMaintain.toAPCD.validateMANDATORYandConstraints()) 
      {
         if (!pMaintain.fromAPCD.isBlank() || !pMaintain.toAPCD.isBlank()) {
            this.PXCONO = currentCONO;
            this.PXDIVI.clear();
            this.MSGID.moveLeftPad(cRefAPCD.heading().getID());
            this.MSGID.move("00");
            PXQRYSLT.PXTYP.move("A ");
            PXQRYSLT.PXFLD.moveLeftPad("E5APCD");
            PXQRYSLT.PXFAL.moveLeft(pMaintain.fromAPCD.get());
            PXQRYSLT.PXTAL.moveLeft(pMaintain.toAPCD.get());
            PXQRYSLT.PXORD = 0;
            PXQRYSLT.PXBRK = 0;
            PXQRYSLT.PXSUM = 0;
            PXQRYSLT.CQRYSLT();
            if (PXQRYSLT.PXQER != ' ') {
               if (PXQRYSLT.PXFNO == 'T') {
                  pMaintain.messages.addError(this.DSPGM, "toAPCD", this.MSGID.toString(), PXQRYSLT.PXMDA);
               } else {
                  pMaintain.messages.addError(this.DSPGM, "fromAPCD", this.MSGID.toString(), PXQRYSLT.PXMDA);
               }
            }
         }
      }
      // Invoice batch head errors
      if (pMaintain.fromIBHE.validateMANDATORYandConstraints() &&
          pMaintain.toIBHE.validateMANDATORYandConstraints()) 
      {
         if (!pMaintain.fromIBHE.isBlank() || !pMaintain.toIBHE.isBlank()) {
            this.PXCONO = currentCONO;
            this.PXDIVI.clear();
            this.MSGID.moveLeftPad(cRefIBHE.heading().getID());
            this.MSGID.move("00");
            PXQRYSLT.PXTYP.move("N ");
            PXQRYSLT.PXFLD.moveLeftPad("E5IBHE");
            PXQRYSLT.PXFNU = (double)pMaintain.fromIBHE.get();
            PXQRYSLT.PXTNU = (double)pMaintain.toIBHE.get();
            PXQRYSLT.PXORD = 0;
            PXQRYSLT.PXBRK = 0;
            PXQRYSLT.PXSUM = 0;
            PXQRYSLT.CQRYSLT();
            if (PXQRYSLT.PXQER != ' ') {
               if (PXQRYSLT.PXFNO == 'T') {
                  pMaintain.messages.addError(this.DSPGM, "toIBHE", this.MSGID.toString(), PXQRYSLT.PXMDA);
               } else {
                  pMaintain.messages.addError(this.DSPGM, "fromIBHE", this.MSGID.toString(), PXQRYSLT.PXMDA);
               }
            }
         }
      }
      // Invoice batch line errors
      if (pMaintain.fromIBLE.validateMANDATORYandConstraints() &&
          pMaintain.toIBLE.validateMANDATORYandConstraints()) 
      {
         if (!pMaintain.fromIBLE.isBlank() || !pMaintain.toIBLE.isBlank()) {
            this.PXCONO = currentCONO;
            this.PXDIVI.clear();
            this.MSGID.moveLeftPad(cRefIBLE.heading().getID());
            this.MSGID.move("00");
            PXQRYSLT.PXTYP.move("N ");
            PXQRYSLT.PXFLD.moveLeftPad("E5IBLE");
            PXQRYSLT.PXFNU = (double)pMaintain.fromIBLE.get();
            PXQRYSLT.PXTNU = (double)pMaintain.toIBLE.get();
            PXQRYSLT.PXORD = 0;
            PXQRYSLT.PXBRK = 0;
            PXQRYSLT.PXSUM = 0;
            PXQRYSLT.CQRYSLT();
            if (PXQRYSLT.PXQER != ' ') {
               if (PXQRYSLT.PXFNO == 'T') {
                  pMaintain.messages.addError(this.DSPGM, "toIBLE", this.MSGID.toString(), PXQRYSLT.PXMDA);
               } else {
                  pMaintain.messages.addError(this.DSPGM, "fromIBLE", this.MSGID.toString(), PXQRYSLT.PXMDA);
               }
            }
         }
      }
      // Report layout
      pMaintain.LITP.validateMANDATORYandConstraints();
      if (pMaintain.LITP.get() == 2 &&
          pMaintain.IBOP.get() == cRefIBOPext.PRINT()) {
         found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, found_FAPIBH, currentCONO, pMaintain.selectDIVI.get(), pMaintain.fromINBN.get());
         if (APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM()) &&
             APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) 
         {
            // MSGID=WLI0501 Report layout &1 is invalid
            pMaintain.messages.addError(this.DSPGM, "LITP", "WLI0501", CRCommon.formatNumForMsg(pMaintain.LITP.get()));
         }
      }
      // Accounting parameters
      if (!updateToAPLactivated && !updateToAPLdeactivated) {
         // Accounting date
         pMaintain.ACDT.validateMANDATORYandConstraints();
         if (!pMaintain.ACDT.isAccessDISABLED() && !pMaintain.ACDT.isBlank()) {
            if (!CRCalendar.lookUpDate_blankDIVI(currentCONO, pMaintain.ACDT.get())) {
               // MSGID=WACD101 Accounting date &1 is invalid
               pMaintain.messages.addError(this.DSPGM, "ACDT", "WACD101", 
                  CRCommon.formatDateForMsg(pMaintain.ACDT.get(), pMaintain.getDTFM(), pMaintain.getDSEP()));
            }
            if (!isAccountingDateWithinLimits()) {
               // Error messages are added in test
            }
         }
         // Voucher text
         pMaintain.VTXT.validateMANDATORYandConstraints();
      }
      // Save query selection to step UPDATE
      pMaintain.work_PXQRYSLT_PXKY.moveFromArray(PXQRYSLT.PXKY);
      pMaintain.work_PXQRYSLT_DSQCMD.moveLeftPad(PXQRYSLT.DSQCMD);
   }

  /**
   * Returns true if accounting is within date limits and period exist
   */
   public boolean isAccountingDateWithinLimits() {
      if (pMaintain.work_INBNpreset) {
         // Only one invoice 
         found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, found_FAPIBH, currentCONO, pMaintain.selectDIVI.get(), pMaintain.fromINBN.get());
         setFamFunction();
         if (!getFamFunction()) {
            // MSGID = XFF0010  FAM function &1/&2 is missing in division &3
            messageData_XFF0010_FEID.moveLeftPad(XXFEID);
            messageData_XFF0010_FNCN.moveLeft(XXFNCN, 3);
            MessageData_XFF0010_DIVI.moveLeftPad(pMaintain.selectDIVI.get());
            pMaintain.messages.addError(this.DSPGM, "", "XFF0010", messageData_XFF0010);
            return false;
         }
         if (!isFamFunctionValid()) {
            // Error in FAM function validation
            // Field, message and message data is set in isFamFunctionValid()
            pMaintain.messages.addError(this.DSPGM, errorFieldName.toStringRTrim(), errorMSGID.toStringRTrim(), errorMessageData);
            return false;
         }
         return true;
      } else {
         // From - to limit for invoice - Test all FAM functions for AP50 - AP54
         return true;
      }
   }

   /**
   * Interaction maintain - step UPDATE.
   */
   public void do_maintain_update() {
      executeTransaction(pMaintain.getTransactionName());
      if (pMaintain.messages.existError()) {
         return;
      }
      // Move updated record back to calling program
      // =========================================
      moveCSYSTP(pMaintain.SYSTP, SYSTP);
   }

   /**
   * Perform explicit transaction APS455FncINmaintain
   */
   @Transaction(name=cPXAPS455FncINmaintain.LOGICAL_NAME, primaryTable="CSYSTP") 
   public void transaction_APS455FncINmaintain() {
      // Declaration
      boolean found_CSYSTP = false;
      // Check if update is possible
      // =========================================
      if (pMaintain.noUpdate.get()) {
         // Move record from calling program
         moveCSYSTP(SYSTP, pMaintain.SYSTP);
      } else {
         SYSTP.setCONO(currentCONO);
         SYSTP.setDIVI().move(LDAZD.DIVI);
         SYSTP.setRESP().move(pMaintain.RESP.get());
         SYSTP.setLIVR().move(pMaintain.LIVR.get());
         found_CSYSTP = SYSTP.CHAIN_LOCK("00", SYSTP.getKey("00"));
         // Update of deleted record
         if (pMaintain.getMode() == cEnumMode.CHANGE && !found_CSYSTP) {
            // MSGID=XDE0001 The record has been deleted by another user
            pMaintain.messages.addError(this.DSPGM, "", "XDE0001");
            return;
         }
         // Update of changed record
         if (pMaintain.getMode() == cEnumMode.CHANGE && found_CSYSTP && pMaintain.work_CHNO != SYSTP.getCHNO()) {
            SYSTP.UNLOCK("00");
            // MSGID=XUP0001 The record has been changed by user &1
            pMaintain.messages.addError(this.DSPGM, "", "XUP0001", SYSTP.getCHID());
            return;
         }
         // Add to an existing record
         if (pMaintain.getMode() == cEnumMode.ADD && found_CSYSTP) {
            SYSTP.UNLOCK("00");
            // MSGID=XAD0001 A record has been entered by user &1
            pMaintain.messages.addError(this.DSPGM, "", "XAD0001", SYSTP.getCHID());
            return;
         }
      }
      // Move parameters to DB-record
      // =========================================
      maintain_update_setValues();
      // Perform update
      // =========================================
      if (!maintain_update_perform(found_CSYSTP)) {
         return;
      }
      // Submit batch job
      submitSelection();
   }

   /**
   * Sets database field values in step UPDATE of interaction maintain.
   * Moves values from the parameter list to the database fields.
   */
   public void maintain_update_setValues() {
      // Save record
      CSYSTP_record.setRecord(SYSTP);
      // Set fields
      APS455DS.setZWDIVI().moveLeftPad(pMaintain.selectDIVI.get());
      APS455DS.setZFSINO().moveLeftPad(pMaintain.fromSINO.get());
      APS455DS.setZTSINO().moveLeftPad(pMaintain.toSINO.get());
      APS455DS.setZFINBN(pMaintain.fromINBN.get());
      APS455DS.setZTINBN(pMaintain.toINBN.get());
      APS455DS.setZFIBTP().moveLeftPad(pMaintain.fromIBTP.get());
      APS455DS.setZTIBTP().moveLeftPad(pMaintain.toIBTP.get());
      APS455DS.setZFSUPA(pMaintain.fromSUPA.get());
      APS455DS.setZTSUPA(pMaintain.toSUPA.get());
      APS455DS.setZFSUNO().moveLeftPad(pMaintain.fromSUNO.get());
      APS455DS.setZTSUNO().moveLeftPad(pMaintain.toSUNO.get());
      APS455DS.setZFIVDT(pMaintain.fromIVDT.get());
      APS455DS.setZTIVDT(pMaintain.toIVDT.get());
      APS455DS.setZFAPCD().moveLeftPad(pMaintain.fromAPCD.get());
      APS455DS.setZTAPCD().moveLeftPad(pMaintain.toAPCD.get());
      APS455DS.setZFIBHE(pMaintain.fromIBHE.get());
      APS455DS.setZTIBHE(pMaintain.toIBHE.get());
      APS455DS.setZFIBLE(pMaintain.fromIBLE.get());
      APS455DS.setZTIBLE(pMaintain.toIBLE.get());
      APS455DS.setZWIBOP(pMaintain.IBOP.get());
      APS455DS.setZWLITP(pMaintain.LITP.get());
      APS455DS.setZWACDT(pMaintain.ACDT.get());
      APS455DS.setZWVTXT().moveLeftPad(pMaintain.VTXT.get());
      APS455DS.setZWMANU(toInt(pMaintain.manualCall.get()));
   }

   /**
   * Effectuate update in interaction maintain.
   * @param found_CSYSTP
   *    Indicates whether the record was found.
   * @return
   *    True if the update was successful.
   */
   public boolean maintain_update_perform(boolean found_CSYSTP) {
      SYSTP.setPARA().moveLeftPad(APS455DS.get());
      if (!pMaintain.noUpdate.get()) {
         if (found_CSYSTP) {
            boolean recordChanged = !SYSTP.equalsRecord(CSYSTP_record);
            if (recordChanged) {
               CSYSTP_setChanged();
               SYSTP.UPDAT("00");
            } else {
               SYSTP.UNLOCK("00");
            }
          } else {
            CSYSTP_setChanged();
            SYSTP.setRGDT(SYSTP.getLMDT());
            SYSTP.setRGTM(movexTime());
            if (!SYSTP.WRITE_CHK("00")) {
               // MSGID=WLI1104 Report version &1 already exists
               pMaintain.messages.addError(this.DSPGM, "LIVR", "WLI1104", SYSTP.getLIVR());
               return false;
            }
         }
      }
      // Change data
      pMaintain.RGDT.set(SYSTP.getRGDT());
      pMaintain.LMDT.set(SYSTP.getLMDT());
      pMaintain.CHID.set().moveLeftPad(SYSTP.getCHID());
      return true;
   }

   /**
   * Submits the query selection as a batch job.
   */
   public void submitSelection() {
      if (pMaintain.noSubmit.get()) {
         // Don't submit the selection
         return;
      }
      // Restore query selection from step VALIDATE
      moveToArray(PXQRYSLT.PXKY, 0, pMaintain.work_PXQRYSLT_PXKY);
      PXQRYSLT.DSQCMD.moveLeftPad(pMaintain.work_PXQRYSLT_DSQCMD);
      // Status limitations per operation
      this.PXCONO = currentCONO;
      this.PXDIVI.clear();
      this.MSGID.moveLeftPad(cRefSUPA.heading().getID());
      this.MSGID.move("00");
      PXQRYSLT.PXTYP.move("N ");
      PXQRYSLT.PXFLD.moveLeftPad("E5SUPA");
      if (pMaintain.IBOP.get() == cRefIBOPext.VALIDATE()) {
         PXQRYSLT.PXFNU = (double)cRefSUPAext.NEW();
         PXQRYSLT.PXTNU = (double)cRefSUPAext.VALIDATED_WITH_ERRORS();
      } else if (pMaintain.IBOP.get() == cRefIBOPext.UPDATE_TO_APL()) {
         PXQRYSLT.PXFNU = (double)cRefSUPAext.APPROVED();
         PXQRYSLT.PXTNU = (double)cRefSUPAext.APPROVED();
      }
      PXQRYSLT.PXORD = 0;
      PXQRYSLT.PXBRK = 0;
      PXQRYSLT.PXSUM = 0;
      PXQRYSLT.CQRYSLT();
      // Finalize query selection
      PXQRYSLT.PXTYP.move("L ");
      PXQRYSLT.CQRYSLT();
      // Get batch job number
      if (pMaintain.BJNO.isBlank()) {
          // New job number
         JBCMD.setBJNO().moveLeftPad(this.getBJNO());
      } else {
         JBCMD.setBJNO().moveLeftPad(pMaintain.BJNO.get());
      }
      JBCMD.DELET("00", JBCMD.getKey("00", 1));
      // Common data - CJBCMD
      JBCMD.clearNOKEY("00");
      JBCMD.setCONO(currentCONO);
      JBCMD.setDIVI().clear();
      JBCMD.setJNA().move(this.DSJNA);
      JBCMD.setJNU(this.DSJNU.getInt());
      JBCMD.setLMDT(movexDate());
      JBCMD.setCHID().move(this.DSUSS);
      JBCMD.setCHNO(1);
      JBCMD.setRGDT(JBCMD.getLMDT());
      JBCMD.setRGTM(movexTime());
      // Build Query Key - CQRYKEY
      moveArray(PXQRYKEY.PXKY, PXQRYSLT.PXKY);
      PXQRYKEY.DSQCMD.move(PXQRYSLT.DSQCMD);
      PXQRYKEY.CQRYKEY();
      // Finalize open query file command - CQRYOPN
      PXQRYOPN.PXFI[0].moveLeftPad("FAPIBH");
      PXQRYOPN.DSQCMD.move(PXQRYKEY.DSQCMD);
      PXQRYOPN.CQRYOPN();
      // Line 01 - save open query file command
      JBCMD.setBJLI().move("01");
      JBCMD.setBJLT().move("QRY");
      JBCMD.setFILE().moveLeftPad("FAPIBH");
      JBCMD.setQCMD().move(PXQRYOPN.DSQCMD);
      JBCMD.setDATA().clear();
      JBCMD.WRITE("00");
      // Select printer data
      if (!pMaintain.BJNO.isBlank()) {
         // Print data already selected in interactive program.
         // No need to select printer data here.
      } else {
         if (pMaintain.IBOP.get() == cRefIBOPext.PRINT()) {
            // Invoice
            CROutput.selectOutputDefs_inBatch(currentCONO, LDAZD.DIVI, JBCMD.getBJNO(),
               "APS456PF", PXMNS210);
            // Detailed printout
            if (pMaintain.LITP.get() == 2) {
               CROutput.selectOutputDefs_inBatch(currentCONO, LDAZD.DIVI, JBCMD.getBJNO(),
                  "APS457PF", PXMNS210);
            }
         }
         if (pMaintain.IBOP.get() == cRefIBOPext.UPDATE_TO_APL()) {
            CROutput.selectOutputDefs_inBatch(currentCONO, LDAZD.DIVI, JBCMD.getBJNO(),
               "GLS041PF", PXMNS210);
         }
      }
      // Select job data
      if (pMaintain.BJNO.isBlank()) {
         CROutput.selectJobAttributes_inBatch(currentCONO, LDAZD.DIVI, JBCMD.getBJNO(),
            /*JOB*/ "APS455", /*PGNM*/ "APS455Sbm", PXMNS230);
      } else {
         // Job data already selected in interactive program
         PXMNS230.PXPGM.moveLeftPad("APS455Sbm");
         PXMNS230.DSQCMD.moveLeftPad(pMaintain.QCMD.get());
         PXMNS230.DSJBDA.clear();
      }
      // Line 90 - Save job data
      JBCMD.setBJLI().move("90");
      JBCMD.setBJLT().move("JOB");
      JBCMD.setFILE().move(PXMNS230.PXPGM);
      JBCMD.setQCMD().move(PXMNS230.DSQCMD);
      JBCMD.setDATA().move(PXMNS230.DSJBDA);
      JBCMD.WRITE("00");
      //   Line 95 - Save key data
      JBCMD.setBJLI().move("95");
      JBCMD.setBJLT().move("KEY");
      JBCMD.setFILE().clear();
      JBCMD.setQCMD().clear();
      JBCMD.setQCMD().moveFromArray(PXQRYKEY.PXKY);
      JBCMD.setDATA().clear();
      JBCMD.WRITE("00");
      //   Line 99 - Save select data
      JBCMD.setBJLI().move("99");
      JBCMD.setBJLT().move("SLT");
      JBCMD.setFILE().clear();
      JBCMD.setQCMD().moveLeftPad(APS455DS.getAPS455DS());
      JBCMD.setDATA().clear();
      JBCMD.WRITE("00");
      // Execute job
      rQCMD.reset();
      rQCMD.set(PXMNS230.DSQCMD);
      rQCMD.set((double)cRefQCMD.length(), 15, 5);
      apCall("QCMDEXC", rQCMD);
      // MSGID=XSB0001 Job &1 has been submitted
      pMaintain.messages.addNotification(this.DSPGM, "", "XSB0001", PXMNS230.PXPGM);
   }

   /**
   * Sets access modes for parameters in interaction maintain for RETRIEVE mode.
   */
   public void maintain_setAccessModeForRetrieve() {
      // Set initial access mode for primary keys and OUT to other parameters
      pMaintain.parameters.setAllOUTAccess();
      // Report version
      pMaintain.LIVR.resetInitialAccessMode();
      // Disable parameters, depending on other parameters
      maintain_setDISABLEDAccessMode();
   }

   /**
   * Sets access modes for parameters in interaction maintain for ADD and CHANGE mode.
   */
   public void maintain_setAccessModeForEntry() {
      // Allow setting of parameter values
      pMaintain.parameters.setAllOPTIONALAccess();
      // Set access mode OUT of primary keys
      pMaintain.LIVR.setOUTAccess();
      // Set access mode OUT of invoice batch no and invoice no
      if (pMaintain.work_INBNpreset) {
         pMaintain.fromSINO.setOUTAccess();
         pMaintain.toSINO.setOUTAccess();
         pMaintain.fromINBN.setOUTAccess();
         pMaintain.toINBN.setOUTAccess();
      }
      // Set MANDATORY access
      // - Division
      if (pMaintain.work_INBNpreset) {
         pMaintain.selectDIVI.setOUTAccess();
      } else {
         pMaintain.selectDIVI.setMANDATORYAccess();
      }
      // - Invoice batch operation
      if (pMaintain.work_IBOPpreset) {
         pMaintain.IBOP.setOUTAccess();
      } else {
         pMaintain.IBOP.setMANDATORYAccess();
      }
      // Accounting date
      pMaintain.ACDT.setMANDATORYAccess();
      // Voucher text
      //if (pMaintain.work_INBNpreset) {
      //   pMaintain.VTXT.setMANDATORYAccess();
      //}
      // Report layout
      pMaintain.LITP.setMANDATORYAccess();
      // Disable parameters, depending on other parameters
      maintain_setDISABLEDAccessMode();
   }

   /**
   * Sets access mode DISABLED for some parameters depending on other parameters.
   */
   public void maintain_setDISABLEDAccessMode() {
      // Disable division if not central user
      if (!CRCommon.isCentralUser()) {
         pMaintain.selectDIVI.setDISABLEDAccess();
      }
      // Disable other selection parameters if INBN is preset
      // or if PRINT operation
      if (pMaintain.work_INBNpreset ||
         pMaintain.IBOP.get() == cRefIBOPext.PRINT()) 
      {
         // Invoice batch type
         pMaintain.fromIBTP.setDISABLEDAccess();
         pMaintain.toIBTP.setDISABLEDAccess();
         pMaintain.fromIBTP.clearValue();
         pMaintain.toIBTP.clearValue();
         // Invoice status
         pMaintain.fromSUPA.setDISABLEDAccess();
         pMaintain.toSUPA.setDISABLEDAccess();
         pMaintain.fromSUPA.clearValue();
         pMaintain.toSUPA.clearValue();
         // Supplier
         pMaintain.fromSUNO.setDISABLEDAccess();
         pMaintain.toSUNO.setDISABLEDAccess();
         pMaintain.fromSUNO.clearValue();
         pMaintain.toSUNO.clearValue();
         // Invoice date
         pMaintain.fromIVDT.setDISABLEDAccess();
         pMaintain.toIVDT.setDISABLEDAccess();
         pMaintain.fromIVDT.clearValue();
         pMaintain.toIVDT.clearValue();
         // Authorized user
         pMaintain.fromAPCD.setDISABLEDAccess();
         pMaintain.toAPCD.setDISABLEDAccess();
         pMaintain.fromAPCD.clearValue();
         pMaintain.toAPCD.clearValue();
         // Invoice batch head errors
         pMaintain.fromIBHE.setDISABLEDAccess();
         pMaintain.toIBHE.setDISABLEDAccess();
         pMaintain.fromIBHE.clearValue();
         pMaintain.toIBHE.clearValue();
         // Invoice batch line errors
         pMaintain.fromIBLE.setDISABLEDAccess();
         pMaintain.toIBLE.setDISABLEDAccess();
         pMaintain.fromIBLE.clearValue();
         pMaintain.toIBLE.clearValue();
      }
      // Disable some parameters depending on operation PRINT 
      if (pMaintain.IBOP.get() == cRefIBOPext.PRINT()) {
         // Supplier invoice no
         pMaintain.toSINO.setDISABLEDAccess();
         pMaintain.toSINO.clearValue();
         // Invoice batch number
         pMaintain.toINBN.setDISABLEDAccess();
         pMaintain.toINBN.clearValue();
      } else {
         // Report layout
         pMaintain.LITP.setDISABLEDAccess();
         pMaintain.LITP.clearValue();
      }
      // Disable accounting parameters if not update to APL
      if (pMaintain.IBOP.get() != cRefIBOPext.UPDATE_TO_APL()) {
         // Accounting date
         pMaintain.ACDT.setDISABLEDAccess();
         pMaintain.ACDT.clearValue();
         // Voucher text
         pMaintain.VTXT.setDISABLEDAccess();
         pMaintain.VTXT.clearValue();
      }
   }

   /**
   * Interaction delete - step INITIATE.
   */
   public void do_delete_initiate() {
      // Get record
      // =========================================
      if (pDelete.passCSYSTP) {
         // Move record from calling program
         moveCSYSTP(SYSTP, pDelete.SYSTP);
         // Set primary keys
         pDelete.RESP.set().moveLeftPad(SYSTP.getRESP());
         pDelete.LIVR.set().moveLeftPad(SYSTP.getLIVR());
      } else {
         // Validate primary key parameters
         // =========================================
         // Responsible
         pDelete.RESP.validateMANDATORYandConstraints();
         // Report version
         pDelete.LIVR.validateMANDATORYandConstraints();
         // Return error messages
         if (pDelete.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pDelete.parameters.setAllDISABLEDAccess();
            pDelete.LIVR.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         SYSTP.setCONO(currentCONO);
         SYSTP.setDIVI().move(LDAZD.DIVI);
         SYSTP.setRESP().move(pDelete.RESP.get());
         SYSTP.setLIVR().move(pDelete.LIVR.get());
         if (!SYSTP.CHAIN("00", SYSTP.getKey("00"))) {
            // MSGID=WLI1103 Report version &1 does not exist
            pDelete.messages.addError(this.DSPGM, "LIVR", "WLI1103", SYSTP.getLIVR());
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pDelete.parameters.setAllDISABLEDAccess();
            pDelete.LIVR.setOUTAccess();
            return;
         }
      }
      // Set parameters
      // =========================================
      delete_initiate_setParameters();
      // Set Access mode
      // =========================================
      delete_setAccessMode();
      // Add notifications
      // =========================================
      // MSGID=WLI1105 Confirm deletion of report version &1
      pDelete.messages.addNotification(this.DSPGM, "LIVR", "WLI1105", pDelete.LIVR.get());
   }

   /**
   * Sets parameters in step INITIATE of interaction delete.
   */
   public void delete_initiate_setParameters() {
   }

   /**
   * Interaction delete - step VALIDATE.
   */
   public void do_delete_validate() {
      // Validate parameters
      // =========================================
      // Nothing to validate
   }

   /**
   * Interaction delete - step UPDATE.
   */
   public void do_delete_update() {
      executeTransaction(pDelete.getTransactionName());
   }

   /**
   * Perform explicit transaction APS455FncINdelete
   */
   @Transaction(name=cPXAPS455FncINdelete.LOGICAL_NAME, primaryTable="CSYSTP") 
   public void transaction_APS455FncINdelete() {
      // Check if delete is possible
      // =========================================
      SYSTP.setCONO(currentCONO);
      SYSTP.setDIVI().move(LDAZD.DIVI);
      SYSTP.setRESP().move(pDelete.RESP.get());
      SYSTP.setLIVR().move(pDelete.LIVR.get());
      if (SYSTP.CHAIN_LOCK("00", SYSTP.getKey("00"))) {
         // Perform update
         // =========================================
         delete_update_perform();
      }
   }

   /**
   * Effectuate update in interaction delete.
   */
   public void delete_update_perform() {
      // Delete record
      SYSTP.DELET("00");
   }

   /**
   * Sets access mode for interaction delete.
   */
   public void delete_setAccessMode() {
      pDelete.parameters.setAllOUTAccess();
   }

   /**
   * Interaction copy - step INITIATE.
   */
   public void do_copy_initiate() {
      // Get record
      // =========================================
      if (pCopy.passCSYSTP) {
         // Move record from calling program
         moveCSYSTP(SYSTP, pCopy.SYSTP);
         // Set primary keys
         pCopy.RESP.set().moveLeftPad(SYSTP.getRESP());
         pCopy.LIVR.set().moveLeftPad(SYSTP.getLIVR());
      } else {
         // Validate primary key parameters
         // =========================================
         // Responsible
         pCopy.RESP.validateMANDATORYandConstraints();
         // Report version
         pCopy.LIVR.validateMANDATORYandConstraints();
         // Return error messages
         if (pCopy.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pCopy.parameters.setAllDISABLEDAccess();
            pCopy.LIVR.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         SYSTP.setCONO(currentCONO);
         SYSTP.setDIVI().move(LDAZD.DIVI);
         SYSTP.setRESP().move(pCopy.RESP.get());
         SYSTP.setLIVR().move(pCopy.LIVR.get());
         if (!SYSTP.CHAIN("00", SYSTP.getKey("00"))) {
            // MSGID=WLI1103 Report version &1 does not exist
            pCopy.messages.addError(this.DSPGM, "LIVR", "WLI1103", SYSTP.getLIVR());
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pCopy.parameters.setAllDISABLEDAccess();
            pCopy.LIVR.setOUTAccess();
            return;
         }
      }
      // Set parameters
      // =========================================
      copy_initiate_setParameters();
      // Set Access mode
      // =========================================
      copy_setAccessMode();
   }

   /**
   * Sets parameters in step INITIATE of interaction copy.
   */
   public void copy_initiate_setParameters() {
      // Copy to Report version
      pCopy.CPLIVR.set().moveLeftPad(SYSTP.getLIVR());
   }

   /**
   * Interaction copy - step VALIDATE.
   */
   public void do_copy_validate() {
      // Validate parameters
      // =========================================
      // Copy to Report version
      pCopy.CPLIVR.validateMANDATORYandConstraints();
      if (pCopy.CPLIVR.get().EQ(pCopy.LIVR.get())) {
         // MSGID=XFT0001 From value is equal to to value
         pCopy.messages.addError(this.DSPGM, "CPLIVR", "XFT0001", pCopy.CPLIVR.get());
      }
   }

   /**
   * Interaction copy - step UPDATE.
   */
   public void do_copy_update() {
      executeTransaction(pCopy.getTransactionName());
      if (pCopy.messages.existError()) {
         return;
      }
   }

   /**
   * Perform explicit transaction APS455FncINcopy
   */
   @Transaction(name=cPXAPS455FncINcopy.LOGICAL_NAME, primaryTable="CSYSTP") 
   public void transaction_APS455FncINcopy() {
      // Check if copy is possible
      // =========================================
      SYSTP.setCONO(currentCONO);
      SYSTP.setDIVI().move(LDAZD.DIVI);
      SYSTP.setRESP().move(pCopy.RESP.get());
      SYSTP.setLIVR().move(pCopy.LIVR.get());
      // Copy of deleted record
      if (!SYSTP.CHAIN("00", SYSTP.getKey("00"))) {
         // MSGID=XDE0001 The record has been deleted by another user
         pCopy.messages.addError(this.DSPGM, "LIVR", "XDE0001");
         return;
      }
      // Perform copy
      // =========================================
      if (!copy_update_perform()) {
         return;
      }
   }

   /**
   * Effectuate update in interaction copy.
   * @return
   *    True if the update was successful.
   */
   public boolean copy_update_perform() {
      SYSTP.setLIVR().move(pCopy.CPLIVR.get());
      CSYSTP_setChanged();
      SYSTP.setRGDT(SYSTP.getLMDT());
      SYSTP.setRGTM(movexTime());
      if (!SYSTP.WRITE_CHK("00")) {
         // MSGID=WLI1104 Report version &1 already exists
          pCopy.messages.addError(this.DSPGM, "CPLIVR", "WLI1104", pCopy.CPLIVR.get());
         return false;
      }
      return true;
   }

   /**
   * Sets access mode for interaction copy.
   */
   public void copy_setAccessMode() {
      // Allow setting of parameter values
      pCopy.parameters.setAllOPTIONALAccess();
      // Set access mode OUT of primary keys
      // - Report version
      pCopy.LIVR.setOUTAccess();
      // Set MANDATORY access
      // - Copy to
      pCopy.CPLIVR.setMANDATORYAccess();
   }

   /**
   * Interaction settings - step INITIATE.
   */
   public void do_settings_initiate() {
      // Validate primary key parameters
      // =========================================
      // Responsible
      pSettings.RESP.validateMANDATORYandConstraints();
      // Return error messages
      if (pSettings.messages.existError()) {
         // Set key parameters to access mode OUT and other parameters to DISABLED.
         pSettings.parameters.setAllDISABLEDAccess();
         return;
      }
      // Read record
      // =========================================
      SYSTR.setCONO(currentCONO);
      SYSTR.setDIVI().move(LDAZD.DIVI);
      SYSTR.setPGNM().moveLeftPad("APS455");
      SYSTR.setRESP().move(pSettings.RESP.get());
      if (!SYSTR.CHAIN("00", SYSTR.getKey("00"))) {
         // Set default values
         SYSTR.clearNOKEY("00");
         settings_initiate_setDefaults();
         SYSTR.setPAR1().moveLeft(XXPAR1);
         // MSGID=XRE0103 Record does not exist
         pSettings.messages.addNotification(this.DSPGM, "", "XRE0103");
      }
      // Set parameters
      // =========================================
      settings_initiate_setParameters();
      // Return error messages
      if (pSettings.messages.existError()) {
         return;
      }
      // Set Access mode
      // =========================================
      settings_setAccessMode();
   }

   /**
   * Sets default values for fields in step INITIATE of interaction settings.
   */
   public void settings_initiate_setDefaults() {
      // Opening panel
      CSSPIC.move(' ');
      // Panel sequence
      CSDSEQ.moveLeftPad(getDefaultPanels());
      // Sorting order
      CSQTTP.move(1);
   }

   /**
   * Sets parameters in step INITIATE of interaction settings.
   */
   public void settings_initiate_setParameters() {
      XXPAR1.moveLeft(SYSTR.getPAR1());
      // Opening panel
      pSettings.SPIC.set().moveLeftPad(CSSPIC.getChar());
      // Panel sequence
      pSettings.DSEQ.set().moveLeftPad(CSDSEQ);
      // Sorting order
      if (pSettings.QTTP.isBlank()) {
         pSettings.QTTP.set(CSQTTP.getInt());
      } else {
         pSettings.QTTP.validateMANDATORYandConstraints();
         // QTTP is not fully validated since it is validated by the interactive program.
      }
   }

   /**
   * Interaction settings - step VALIDATE.
   */
   public void do_settings_validate() {
      // Validate parameters
      // =========================================
      // Opening panel
      pSettings.SPIC.validateMANDATORYandConstraints();
      // Panel sequence
      pSettings.DSEQ.validateMANDATORYandConstraints();
      CRCommon.checkPanelSequence(pSettings.DSEQ.get(), getAllowedPanels(), pSettings.messages, "DSEQ", this.DSPGM);
   }

   /**
   * Interaction settings - step UPDATE.
   */
   public void do_settings_update() {
      executeTransaction(pSettings.getTransactionName());
      if (pSettings.messages.existError()) {
         return;
      }
   }

   /**
   * Perform explicit transaction APS455FncINsettings
   */
   @Transaction(name=cPXAPS455FncINsettings.LOGICAL_NAME, primaryTable="CSYSTR") 
   public void transaction_APS455FncINsettings() {
      // Declaration
      boolean found_CSYSTR = false;
      // Check if update is possible
      // =========================================
      SYSTR.setCONO(currentCONO);
      SYSTR.setDIVI().move(LDAZD.DIVI);
      SYSTR.setPGNM().moveLeftPad("APS455");
      SYSTR.setRESP().move(pSettings.RESP.get());
      found_CSYSTR = SYSTR.CHAIN_LOCK("00", SYSTR.getKey("00"));
      // Move parameters to DB-record
      // =========================================
      settings_update_setValues();
      // Perform update
      // =========================================
      if (!settings_update_perform(found_CSYSTR)) {
         return;
      }
   }

   /**
   * Sets database field values in step UPDATE of interaction settings.
   * Moves values from the parameter list to the database fields.
   */
   public void settings_update_setValues() {
      // Opening panel
      CSSPIC.move(pSettings.SPIC.get());
      // Panel sequence
      CSDSEQ.move(pSettings.DSEQ.get());
      // Sorting order
      CSQTTP.move(pSettings.QTTP.get());
   }

   /**
   * Effectuate update in interaction settings.
   * @param found_CSYSTR
   *    Indicates whether the record was found.
   * @return
   *    True if the update was successful.
   */
   public boolean settings_update_perform(boolean found_CSYSTR) {
      SYSTR.setPAR1().moveLeftPad(XXPAR1);
      SYSTR.setLMDT(movexDate());
      if (found_CSYSTR) {
         SYSTR.UPDAT("00");
      } else {
         SYSTR.setRGDT(SYSTR.getLMDT());
         if (!SYSTR.WRITE_CHK("00")) {
            // MSGID=WRE0104 Responsible &1 already exists
            pSettings.messages.addError(this.DSPGM, "", "WRE0104", SYSTR.getRESP());
            return false;
         }
      }
      return true;
   }

   /**
   * Sets access mode for interaction settings.
   */
   public void settings_setAccessMode() {
      // Allow setting of parameter values
      pSettings.parameters.setAllOPTIONALAccess();
      // Set access mode OUT of primary keys
      // - No primary keys
      // - Sorting order
      pSettings.QTTP.setOUTAccess();
      // Set MANDATORY access
      // - Opening panel
      pSettings.SPIC.setMANDATORYAccess();
      // - Panel sequence
      pSettings.DSEQ.setMANDATORYAccess();
   }

   /**
   * Sets fields LMDT, CHID, CHNO for the record.
   */
   public void CSYSTP_setChanged() {
      SYSTP.setLMDT(movexDate());
      SYSTP.setCHID().move(this.DSUSS);
      SYSTP.setCHNO(SYSTP.getCHNO() + 1);
   }

   /**
   * Moves the record from one DB-interface to another.
   * @param to
   *    The DB-interface to move data to.
   * @param from
   *    The DB-interface to move data from.
   */
   public void moveCSYSTP(mvx.db.dta.CSYSTP to, mvx.db.dta.CSYSTP from) {
      if (to == null || from == null) {
         return;
      }      
      CSYSTP_record.reset();
      CSYSTP_record.setMDBRecord(from);
      CSYSTP_record.reset();
      CSYSTP_record.getMDBRecord(to);
   }

  /**
   * Sets FAM entry ID and FAM function in fields XXFEID and XXFNCN
   */
   public void setFamFunction() {
      switch (0) {
         default:
            if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING())) {
               //   From MPFAMF (PPS114) if self billing
               PFAMF.setCONO(APIBH.getCONO());
               PFAMF.setDIVI().moveLeftPad(APIBH.getDIVI());
               PFAMF.setFEID().moveLeftPad("AP52");
               PFAMF.setSUNO().moveLeftPad(APIBH.getSUNO());
               if (PFAMF.CHAIN("00", PFAMF.getKey("00", 4))) {
                  XXFNCN = PFAMF.getFNCN();
                  XXFEID.move("AP52");
               } else {
                  PFAMF.setSUNO().clear();
                  if (PFAMF.CHAIN("00", PFAMF.getKey("00", 4))) {
                     XXFNCN = PFAMF.getFNCN();
                     XXFEID.move("AP52");
                  } else {
                     XXFEID.move("AP50");
                     XXFNCN = 1;
                  }
               }
               break;
            }
            if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
               //   Debit note
               XXFEID.move("AP51");
               //   Read AP Standard document
               found_CSYTAB_SDAP = cRefSDAPext.getCSYTAB_SDAP(SYTAB, found_CSYTAB_SDAP, APIBH.getCONO(), APIBH.getDIVI(), APIBH.getSDAP());
               cRefSDAPext.setDSSDAP(SYTAB, DSSDAP);
               if (found_CSYTAB_SDAP &&
                   DSSDAP.getYUFNCN() != 0) { 
                  XXFNCN = DSSDAP.getYUFNCN();
               } else {
                  XXFNCN = 1;
               }
               break;
            }
            if (APIBH.getIBTP().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
               //   Prepayment pre invoice
               XXFEID.move("AP53");
               XXFNCN = 1;
               break;
            }
            if (APIBH.getIBTP().EQ(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) {
               //   Prepayment final invoice
               XXFEID.move("AP54");
               XXFNCN = 1;
               break;
            }
            XXFEID.move("AP50");
            XXFNCN = 1;
            break;
      }
   }

  /**
   * Sets FAM entry ID and FAM function in fields XXFEID and XXFNCN
   */
   public boolean getFamFunction() {
      value_FEID.moveLeftPad(XXFEID);
      value_FNCN.moveLeft(XXFNCN, cRefFNCN.length());
      found_CSYTAB_FFNC = cRefFFNCext.getCSYTAB_FFNC(SYTAB, found_CSYTAB_FFNC, currentCONO, pMaintain.selectDIVI.get(), value_STKY_FEID);
      cRefFFNCext.setDSFFNC(SYTAB, DSFFNC);
      if (!found_CSYTAB_FFNC) {
         return false;
      }
      return true;
   }

  /**
   * Retun true if FAM function is valid for accounting date
   */
   public boolean isFamFunctionValid() {
      int period = 0;
      //   - Acc. date must be in range of date limits
      if (pMaintain.ACDT.get() >= DSFFNC.getDFFRDT() &&
          pMaintain.ACDT.get() <= DSFFNC.getDFTODT()) {
      // dummy then part
      } else {
         MessageData_XDT0005_WDFRDT.moveLeft(CRCalendar.convertDate(pMaintain.selectDIVI.get(), DSFFNC.getDFFRDT(), LDAZD.DTFM, ' '));
         MessageData_XDT0005_WDTODT.moveLeft(CRCalendar.convertDate(pMaintain.selectDIVI.get(), DSFFNC.getDFTODT(), LDAZD.DTFM, ' '));
         MessageData_XDT0005_WDDATE.moveLeft(CRCalendar.convertDate(pMaintain.selectDIVI.get(), pMaintain.ACDT.get(), LDAZD.DTFM, ' '));
         //   MSGID=XDT0005 Accounting date &3 is not within the valid range, which is &1 - &2
         errorFieldName.moveLeftPad("ACDT");
         errorMSGID.moveLeftPad("XDT0005");
         errorMessageData.moveLeftPad(MessageData_XDT0005);
         return false;
      }
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.selectDIVI.get());
      CRCalendar.lookUpDate(currentCONO, pMaintain.selectDIVI.get(), pMaintain.ACDT.get());
      if (MNDIV.getPTFA() == 1) {
         period = CRCalendar.getPeriodType1();
      }
      if (MNDIV.getPTFA() == 2) {
         period = CRCalendar.getPeriodType2();
      }
      if (MNDIV.getPTFA() == 3) {
         period = CRCalendar.getPeriodType3();
      }
      if (MNDIV.getPTFA() == 4) {
         period = CRCalendar.getPeriodType4();
      }
      if (MNDIV.getPTFA() == 5) {
         period = CRCalendar.getPeriodType5();
      }
      this.PXCONO = currentCONO;
      this.PXDIVI.move(pMaintain.selectDIVI.get());
      PXCHKPER.PXPETP = MNDIV.getPTFA();
      PXCHKPER.PXPERI = period;
      PXCHKPER.PXPERF.move("CYP6");
      PXCHKPER.CCHKPER();
      if (PXCHKPER.PXPEER == 1) {
         //   MSGID=WAC0803 Period &1 does not exist
         errorFieldName.moveLeftPad("ACDT");
         errorMSGID.moveLeftPad("WAC0803");
         errorMessageData.moveLeftPad(period, 6);
         return false;
      }
      return true;
   }

   /**
   * Initiation of function program
   */
   public void INIT() { 	
      // Init keys
      this.PXCONO = currentCONO;
      this.PXDIVI.clear(); 	
      SYSTP.setPGNM().moveLeftPad("APS455");
      // Create record
      if (CSYSTP_record == null) {
         CSYSTP_record = SYSTP.getEmptyRecord();
      }
   }

   /**
   * Throws RuntimeException causing program dump.
   * pListError is called only when no valid parameter list is
   * passed to the function program.
   */
   public void pListError() {
      throw new RuntimeException("No valid parameter list received.");
   }

   // Movex MDB definitions
   public mvx.db.dta.CSYSTR SYSTR;
   public mvx.db.dta.CSYSTP SYSTP;
   public mvx.db.dta.CMNCMP MNCMP;
   public mvx.db.dta.CMNDIV MNDIV;
   public mvx.db.dta.CJBCMD JBCMD;
   public mvx.db.dta.FAPIBH APIBH;
   public mvx.db.dta.MPFAMF PFAMF;
   public mvx.db.dta.CSYTAB SYTAB;
   public mvx.db.dta.CIDMAS IDMAS;

   public void initMDB() {
      SYSTR = (mvx.db.dta.CSYSTR)getMDB("CSYSTR", SYSTR);
      SYSTP = (mvx.db.dta.CSYSTP)getMDB("CSYSTP", SYSTP);
      MNCMP = (mvx.db.dta.CMNCMP)getMDB("CMNCMP", MNCMP);
      MNDIV = (mvx.db.dta.CMNDIV)getMDB("CMNDIV", MNDIV);
      JBCMD = (mvx.db.dta.CJBCMD)getMDB("CJBCMD", JBCMD);
      APIBH = (mvx.db.dta.FAPIBH)getMDB("FAPIBH", APIBH);
      PFAMF = (mvx.db.dta.MPFAMF)getMDB("MPFAMF", PFAMF);
      SYTAB = (mvx.db.dta.CSYTAB)getMDB("CSYTAB", SYTAB);
      IDMAS = (mvx.db.dta.CIDMAS)getMDB("CIDMAS", IDMAS);
   }

   // Entry parameters
   @ParameterList
   public cPXAPS455FncINmaintain pMaintain = null;
   @ParameterList
   public cPXAPS455FncINdelete pDelete = null;
   @ParameterList
   public cPXAPS455FncINcopy pCopy = null;
   @ParameterList
   public cPXAPS455FncINsettings pSettings = null;

   public MvxRecord CSYSTP_record = null;
   public cCRCommon CRCommon = new cCRCommon(this);
   public cCRCalendar CRCalendar = new cCRCalendar(this);
   public cCROutput CROutput = new cCROutput(this);
   public sAPS455DS APS455DS = new sAPS455DS(this);
   public sDSFFNC DSFFNC = new sDSFFNC(this);
   public sDSSDAP DSSDAP = new sDSSDAP(this);
   public MvxRecord rQCMD = new MvxRecord();
   public cPLCHKAD PLCHKAD = new cPLCHKAD(this);
   public cPXQRYSLT PXQRYSLT = new cPXQRYSLT(this);
   public cPXQRYKEY PXQRYKEY = new cPXQRYKEY(this);
   public cPXQRYOPN PXQRYOPN = new cPXQRYOPN(this);
   public cPXMNS210 PXMNS210 = new cPXMNS210(this);
   public cPXMNS230 PXMNS230 = new cPXMNS230(this);
   public cPXCHKPER PXCHKPER = new cPXCHKPER(this);
   public boolean found_CSYSTR;
   public boolean found_CMNCMP;
   public boolean found_CMNDIV;
   public boolean found_FAPIBH;
   public boolean found_CIDMAS;
   public boolean found_CSYTAB_FFNC;
   public boolean found_CSYTAB_SDAP;
   public int XXPTFA;
   public int XXFNCN;
   public MvxString errorMessageData = new MvxString(100); // 100 pos can be used for error message
   public MvxString errorMSGID = cRefMSID.likeDef();
   public MvxString errorFieldName = cRefTX40.likeDef(); // 40 pos can be used for field name
   public MvxString XXFEID = cRefFEID.likeDef();

   public MvxStruct rvalue_STKY_FEID = new MvxStruct(10);
   public MvxString value_STKY_FEID = rvalue_STKY_FEID.newString(0, 10);
   public MvxString value_FEID = rvalue_STKY_FEID.newString(0, 4);
   public MvxString value_FNCN = rvalue_STKY_FEID.newInt(4, 3);

   public MvxStruct rXXPAR1 = new MvxStruct(100);
   public MvxString XXPAR1 = rXXPAR1.newString(0, 100);
   public MvxString CSSPIC = rXXPAR1.newChar(0);
   public MvxString CSDSEQ = rXXPAR1.newString(cRefSPIC.length(), cRefPSEQ.length());
   public MvxString CSQTTP = rXXPAR1.newInt(cRefSPIC.length() + cRefPSEQ.length(), cRefQTTP.length());

   //*STRUCDEF rMessageData_XDT0005{
   public MvxStruct rMessageData_XDT0005 = new MvxStruct(18);
   public MvxString MessageData_XDT0005 = rMessageData_XDT0005.newString(0, 18);
   public MvxString MessageData_XDT0005_WDFRDT = rMessageData_XDT0005.newString(0, 6);
   public MvxString MessageData_XDT0005_WDTODT = rMessageData_XDT0005.newString(6, 6);
   public MvxString MessageData_XDT0005_WDDATE = rMessageData_XDT0005.newString(12, 6);

   //*STRUCDEF rMessageData_XFF010{
   public MvxStruct rMessageData_XFF0010 = new MvxStruct(cRefFEID.length() + 3 + cRefDIVI.length());
   public MvxString messageData_XFF0010 = rMessageData_XFF0010.newString(0, cRefFEID.length() + 3 + cRefDIVI.length());
   public MvxString messageData_XFF0010_FEID = rMessageData_XFF0010.newString(0, cRefFEID.length());
   public MvxString messageData_XFF0010_FNCN = rMessageData_XFF0010.newInt(cRefFEID.length(), 3);
   public MvxString MessageData_XFF0010_DIVI = rMessageData_XFF0010.newString(cRefFEID.length() + 3 , cRefDIVI.length());
   
   //*STRUCDEF rXXVTXT{
   public MvxStruct rXXVTXT = new MvxStruct(40);
   public MvxString XXVTXT = rXXVTXT.newString(0, 40);
   public MvxString XQSUNO = rXXVTXT.newString(0, 10);
   public MvxString XXSINO = rXXVTXT.newString(11, 14);
   public MvxString XXINYR = rXXVTXT.newInt(25, 4);
   public MvxString XXALSU = rXXVTXT.newString(30, 10);

   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(SYSTR);
      v.addElement(SYSTP);
      v.addElement(MNCMP);
      v.addElement(MNDIV);
      v.addElement(JBCMD);
      v.addElement(APIBH);
      v.addElement(PFAMF);
      v.addElement(SYTAB);
      v.addElement(IDMAS);
      v.addElement(pMaintain);
      v.addElement(pDelete);
      v.addElement(pCopy);
      v.addElement(pSettings);
      v.addElement(CRCommon);
      v.addElement(CRCalendar);
      v.addElement(CROutput);
      v.addElement(APS455DS);
      v.addElement(DSFFNC);
      v.addElement(DSSDAP);
      v.addElement(PLCHKAD);
      v.addElement(PXQRYSLT);
      v.addElement(PXQRYKEY);
      v.addElement(PXQRYOPN);
      v.addElement(PXMNS210);
      v.addElement(PXMNS230);
      v.addElement(PXCHKPER);
      v.addElement(rXXPAR1);
      v.addElement(errorFieldName);
      v.addElement(errorMSGID);
      v.addElement(errorMessageData);
      v.addElement(XXFEID);
      v.addElement(rvalue_STKY_FEID);
      v.addElement(rMessageData_XDT0005);
      v.addElement(rMessageData_XFF0010);
      v.addElement(rXXVTXT);
      return version;
   }

   public void clearInstance() {
      super.clearInstance();
      found_CSYSTR = false;
      found_CMNCMP = false;
      found_CMNDIV = false;
      found_FAPIBH = false;
      found_CIDMAS = false;
      found_CSYTAB_FFNC = false;
      found_CSYTAB_SDAP = false;
      XXPTFA = 0;
      XXFNCN = 0;
   }

   public final static String PANELS_REQUIRED_FOR_ADD = "E";

   public final static String ALLOWED_PANELS = PANELS_REQUIRED_FOR_ADD + "-";

   public final static String DEFAULT_PANELS = PANELS_REQUIRED_FOR_ADD;

   /**
   * If you override this method, then you must also override the corresponding method in the interactive program.
   * @return
   *    The panels that are required in the interactive program when 
   *    adding a new record.
   */
   public static String getPanelsRequiredForAdd() {
      return PANELS_REQUIRED_FOR_ADD;
   }

   /**
   * If you override this method, then you must also override the corresponding method in the interactive program.
   * @return
   *    The panels that are allowed to use in the interactive program.
   */
   public static String getAllowedPanels() {
      return ALLOWED_PANELS;
   }

   /**
   * If you override this method, then you must also override the corresponding method in the interactive program.
   * @return
   *    The panels that are defaulted in the interactive program.
   */
   public static String getDefaultPanels() {
      return DEFAULT_PANELS;
   }

public final static String _version="15";

public final static String _release="1";

public final static String _spLevel="2";

public final static String _spNumber="";

public final static String _GUID="4442367233634db4935484E8821EBF66";

public final static String _tempFixComment="";

public final static String _build="000000000000075";

public final static String _pgmName="APS455Fnc";

   public String getVersion() {
      return _version;
   } //·end of method getVersion

   public String getRelease() {
      return _release;
   } //·end of method getRelease

   public String getSpLevel() {
      return _spLevel;
   } //·end of method getSpLevel

   public String getSpNumber() {
      return _spNumber;
   } //·end of method getSpNumber

   public String getGUID() {
      return _GUID;
   } //·end of method getGUID

   public String getTempFixComment() {
      return _tempFixComment;
   } //·end of method getTempFixComment

   public String getVersionInformation() {
      return _version + '.' + _release + '.' + _spLevel + ':' + _spNumber;
   } //·end of method getVersionInformation

   public String getBuild() {
      return (_version + _release + _build + "      " +  _pgmName + "                                   ").substring(0,34);
   } //·end of method getBuild

   public String [][] getStandardModification() {
      return _standardModifications;
   } //·end of method [][] getStandardModification

  public final static String [][] _standardModifications={};
} 	 	
