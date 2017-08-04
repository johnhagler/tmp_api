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
import mvx.db.common.Expression;
import mvx.db.common.FieldSelection;

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
*<BR><B><FONT SIZE=+2>Fnc: Manage Supplier invoice batch</FONT></B><BR><BR>
*
* <PRE>
* Function program APS450Fnc is called by interactive program APS450 to
* mangage FAPIBH - Supplier invoice batch.
*
* The following interactions and operations are available:
*
* Interaction maintain
* --------------------
* parameter list: cPXAPS450FncINmaintain (instantiated as pMaintain )
* Interaction used when adding, changing or retrieving a invoice batch number.
* Returns any errors or notifications.
*
*
* Interaction delete
* ------------------
* parameter list: cPXAPS450FncINdelete (instantiated as pDelete )
* Interaction used when deleting a invoice batch number.
* Returns any errors or notifications.
*
*
* Interaction copy
* ----------------
* parameter list: cPXAPS450FncINcopy (instantiated as pCopy )
* Interaction used when copying a invoice batch number.
* Returns any errors or notifications.
*
*
* Interaction settings
* --------------------
* parameter list: cPXAPS450FncINsettings (instantiated as pSettings )
* Interaction used when setting parameters for APS450 function.
* Returns any errors or notifications.
*
* Returns notification 'XRE0103 - Record does not exist' in step INITIATE if there
* is no settings record for the responsible. In that case default values are
* returned for the parameters and a record will be written in step UDPATE.
*
* Interaction changeDivision
* --------------------------
* parameter list: cPXAPS450FncINchangeDivision (instantiated as pChangeDivision )
* Interaction used to change division.
* Returns any errors or notifications.
*
* Interaction rejectInvoice
* ------------------------
* parameter list: cPXAPS450FncINrejectInvoice (instantiated as pRejectInvoice )
* Interaction used to reject Invoice.
* Returns any errors or notifications.
*
* Interaction approveInvoice
* ------------------------
* parameter list: cPXAPS450FncINapproveInvoice (instantiated as pApproveInvoice )
* Interaction used to adjust or approve Invoice.
* Returns any errors or notifications.
*
* Interaction acknowledge
* ------------------------
* parameter list: cPXAPS450FncINacknowledge (instantiated as pAcknowledge)
* Interaction used to acknowledge Invoice (supplier claim or supplier claim request).
* Sets credit number and your reference.
* Returns any errors or notifications.
*
* Operation lockForPrint
* -------------------------
* parameter list: cPXAPS450FncOPlockForPrint (instantiated as pLockForPrint )
* Operation used for locking invoice batch number for printing.
* Returns any errors or notifications.
*
* Operation lockForUpdate
* ------------------------
* parameter list: cPXAPS450FncOPlockForUpdate (instantiated as pLockForUpdate )
* Operation used for locking invoice batch number for update.
* Returns any errors or notifications.
*
* Operation lockForUpdAPL
* ------------------------
* parameter list: cPXAPS450FncOPlockForUpdAPL (instantiated as pLockForUpdAPL )
* Operation used for locking invoice batch number for update of APL.
* Returns any errors or notifications.
*
* Operation lockForAdjLine
* ------------------------
* parameter list: cPXAPS450FncOPlockForAdjLine (instantiated as pLockForAdjLine )
* Operation used for locking invoice batch number for adjust of line.
* Returns any errors or notifications.
*
* Operation unlock
* ------------------------
* parameter list: cPXAPS450FncOPunlock (instantiated as pUnlock )
* Operation used for unlocking invoice batch number.
* Returns any errors or notifications.
*
* Operation checkIfWIP (check If Work In Progress) 
* ------------------------
* parameter list: cPXAPS450FncOPcheckIfWIP (instantiated as pCheckIfWIP )
* Operation used for checking if invoice batch number is free to use or Work in progress.
* Returns any errors or notifications.
*
* Operation validateInv (validate invoice head and lines) 
* ------------------------
* parameter list: cPXAPS450FncOPvalidateInv (instantiated as pValidateInv )
* Validates batch invoice head and lines + sets error indication and error message in FAPIBH/FAPIBL
* Returns any errors or notifications.
*
* Operation reversePrintout
* -------------------------
* parameter list: cPXAPS450FncOPreversePrintout (instantiated as pReversePrintout )
* Operation used to set invoice status (SUPA) to 20 again.
* Returns any errors or notifications.
*
* Operation resetToStatusNew
* ------------------------
* parameter list: cPXAPS450FncOPresetToStatusNew (instantiated as pResetToStatusNew )
* Operation used for setting status of invoice batch number to 10 - NEW.
* Returns any errors or notifications.
*
* </PRE>
*/
public class APS450Fnc extends Function
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
      // Interaction Change division
      // =========================================
      } else if (pChangeDivision != null) {
         if (pChangeDivision.getStep() == cEnumStep.INITIATE) {
            do_changeDivision_initiate();
         } else if (pChangeDivision.getStep() == cEnumStep.VALIDATE) {
            do_changeDivision_validate();
         } else if (pChangeDivision.getStep() == cEnumStep.UPDATE) {
            do_changeDivision_update();
         }
      // Interaction Reject invoice
      // =========================================
      } else if (pRejectInvoice != null) {
         if (pRejectInvoice.getStep() == cEnumStep.INITIATE) {
            do_rejectInvoice_initiate();
         } else if (pRejectInvoice.getStep() == cEnumStep.VALIDATE) {
            do_rejectInvoice_validate();
         } else if (pRejectInvoice.getStep() == cEnumStep.UPDATE) {
            do_rejectInvoice_update();
         }
      // Interaction Approve invoice
      // ===========================
      } else if (pApproveInvoice != null) {
         if (pApproveInvoice.getStep() == cEnumStep.INITIATE) {
            do_approveInvoice_initiate();
         } else if (pApproveInvoice.getStep() == cEnumStep.VALIDATE) {
            do_approveInvoice_validate();
         } else if (pApproveInvoice.getStep() == cEnumStep.UPDATE) {
            do_approveInvoice_update();
         }
      // Interaction Acknowledge
      // ===========================
      } else if (pAcknowledge != null) {
         if (pAcknowledge.getStep() == cEnumStep.INITIATE) {
            do_acknowledge_initiate();
         } else if (pAcknowledge.getStep() == cEnumStep.VALIDATE) {
            do_acknowledge_validate();
         } else if (pAcknowledge.getStep() == cEnumStep.UPDATE) {
            do_acknowledge_update();
         }
      // Operation Lock for printing
      // =========================================
      } else if (pLockForPrint != null) {
         do_lockForPrint();
      // Operation Lock for update
      // =========================================
      } else if (pLockForUpdate != null) {
         do_lockForUpdate();
         // Operation Lock for update of APL
      // =========================================
      } else if (pLockForUpdAPL != null) {
         do_lockForUpdateOfAPL();
      // Operation Lock for adjust of line
      // =========================================
      } else if (pLockForAdjLine != null) {
         do_lockForAdjLine();
      // Operation Unlock
      // =========================================
      } else if (pUnlock != null) {
         do_unlock();
      // Operation Check if work in progress
      // =========================================
      } else if (pCheckIfWIP != null) {
         do_checkIfWIP();
      // Operation Validate Invoice
      // =========================================
      } else if (pValidateInv != null) {
         do_validateInvoice();
      // Operation Reverse printout
      // =========================================
      } else if (pReversePrintout != null) {
         do_reversePrintout();
      // Operation Reset to status new
      // =========================================
      } else if (pResetToStatusNew != null) {
         do_resetToStatusNew();
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
      boolean found_FAPIBH = false;
      // Get record
      // =========================================
      if (pMaintain.passFAPIBH && pMaintain.getMode() != cEnumMode.ADD) {
         // Move record from calling program
         moveFAPIBH(APIBH, pMaintain.APIBH);
         // Set primary keys
         pMaintain.DIVI.set().moveLeftPad(APIBH.getDIVI());
         pMaintain.INBN.set(APIBH.getINBN());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pMaintain.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         if (pMaintain.getMode() == cEnumMode.ADD) {
            // Invoice batch number is set automatically for ADD mode (just before writing the record)
            pMaintain.INBN.clearValue();
            pMaintain.INBN.setOUTAccess();
         } else {
            pMaintain.INBN.setMANDATORYAccess();
         }
         pMaintain.INBN.validateMANDATORYandConstraints();
         // Return error messages
         if (pMaintain.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pMaintain.parameters.setAllDISABLEDAccess();
            pMaintain.DIVI.setOUTAccess();
            pMaintain.INBN.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBH.setCONO(currentCONO);
         APIBH.setDIVI().move(pMaintain.DIVI.get());
         APIBH.setINBN(pMaintain.INBN.get());
         found_FAPIBH = APIBH.CHAIN("00", APIBH.getKey("00"));
         if (pMaintain.getMode() == cEnumMode.ADD) {
            // - no check on record already exists since INBN is retrieved at update.
            // Set default values
            APIBH.clearNOKEY("00");
            maintain_initiate_setDefaults();
         } else if (!found_FAPIBH) {
            // MSGID=WINBN03 Invoice batch number &1 does not exist
            pMaintain.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         }
         // Return error messages
         if (pMaintain.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pMaintain.parameters.setAllDISABLEDAccess();
            pMaintain.DIVI.setOUTAccess();
            pMaintain.INBN.setOUTAccess();
            return;
         }
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pMaintain.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pMaintain.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      if (pMaintain.getMode() == cEnumMode.CHANGE) {
         if (checkIfWIP(pMaintain.messages, alreadyLockedByJob)) {
            pMaintain.BIST.set(APIBH.getBIST());
         }
      }
      if (pMaintain.getMode() == cEnumMode.RETRIEVE) {
         if (checkIfWIPinExplicitTransaction()) {
            pMaintain.BIST.set(APIBH.getBIST());
         }
      }
      // Check status and work in progress
      if (!statusOKForUpdate(APIBH) && pMaintain.getMode() == cEnumMode.CHANGE) {
         // XOP0200 = Change
         messageData_X_00051_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("XOP0200", "MVXCON")));
         messageData_X_00051_OPT2.moveLeftPad(" 2");
         messageData_X_00051_SUPA.moveLeftPad(CRCommon.formatNumForMsg(APIBH.getSUPA()));
         // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
         pMaintain.messages.addError(this.DSPGM, "INBN", "X_00051", messageData_X_00051);
      }
      // Return error messages
      if (pMaintain.messages.existError()) {
         // Set key parameters to access mode OUT and other parameters to DISABLED.
         pMaintain.parameters.setAllDISABLEDAccess();
         pMaintain.DIVI.setOUTAccess();
         pMaintain.INBN.setOUTAccess();
         return;
      }
      // Set dynamic constraints for IBTP
      setDynamicConstraintsForIBTP(pMaintain.DIVI.get());
      // Validate optional/mandatory parameters in ADD mode
      // =========================================
      if (pMaintain.getMode() == cEnumMode.ADD) {
         // Indicate that default values should be retrieved for parameters related to main parameters
         pMaintain.work_getMainDefaults = true;
         // Invoice batch type - may be left blank in step INITIATE
         if (!pMaintain.IBTP.isBlank()) {
            maintain_validateIBTP();
            APIBH.setIBTP().moveLeftPad(pMaintain.IBTP.get());
         }
         // Supplier - may be left blank in step INITIATE
         if (!pMaintain.SUNO.isBlank()) {
            maintain_validateSUNO();
            APIBH.setSUNO().moveLeftPad(pMaintain.SUNO.get());
         }
         // Payee - may be left blank in step INITIATE
         if (!pMaintain.SPYN.isBlank()) {
            maintain_validateSPYN();
            APIBH.setSPYN().moveLeftPad(pMaintain.SPYN.get());
         }
         // Invoice date - may be left blank in step INITIATE
         if (!pMaintain.IVDT.isBlank()) {
            validateIVDT(pMaintain.IVDT, pMaintain.DIVI.get(), pMaintain.partialVld.get(), pMaintain.messages, pMaintain);
            APIBH.setIVDT(pMaintain.IVDT.get());
         }
         // Invoice matching - may be left blank in step INITIATE
         if (!pMaintain.IMCD.isBlank()) {
            maintain_validateIMCD();
            APIBH.setIMCD(pMaintain.IMCD.get().getChar());
         }
         // Purchase order number - may be left blank in step INITIATE
         if (!pMaintain.PUNO.isBlank()) {
            maintain_validatePUNO();
            APIBH.setPUNO().moveLeftPad(pMaintain.PUNO.get());
         }
         // Currency - may be left blank in step INITIATE
         if (!pMaintain.CUCD.isBlank()) {
            if (!pMaintain.partialVld.get()) {
               pMaintain.CUCD.validateMANDATORYandConstraints();
               found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, pMaintain.CUCD.get());
               pMaintain.CUCD.validateExists(found_CSYTAB_CUCD);
            }
            APIBH.setCUCD().moveLeftPad(pMaintain.CUCD.get());
         }
      }
      // Return error messages
      if (pMaintain.messages.existError()) {
         // Set key/optional/mandatory parameters to access mode OUT
         // and other parameters to DISABLED.
         pMaintain.parameters.setAllDISABLEDAccess();
         pMaintain.DIVI.setOUTAccess();
         pMaintain.INBN.setOUTAccess();
         pMaintain.SPYN.setOUTAccess();
         pMaintain.SUNO.setOUTAccess();
         pMaintain.IVDT.setOUTAccess();
         pMaintain.IBTP.setOUTAccess();
         pMaintain.IMCD.setOUTAccess();
         pMaintain.PUNO.setOUTAccess();
         pMaintain.CUCD.setOUTAccess();
         return;
      }
      // Set parameters
      // =========================================
      maintain_initiate_setParameters();
      // Get defaults
      // =========================================
      if (pMaintain.getMode() == cEnumMode.ADD && pMaintain.work_mainParamsSet) {
         // All main parameters have been set so default values for related parameters should be retrieved
         maintain_validate_setDefaults();
      }
      // Set Access mode
      // =========================================
      if (pMaintain.getMode() == cEnumMode.RETRIEVE) {
         maintain_setAccessModeForRetrieve();
      } else {
         maintain_setAccessModeForEntry();
      }
   }

   /**
   * Sets default values for fields in ADD mode step INITIATE of interaction maintain.
   * This method is to ensure that all fields have valid values after clearNOKEY has
   * been performed.
   */
   public void maintain_initiate_setDefaults() {
      APIBH.setDIVI().moveLeftPad(pMaintain.DIVI.get());
      APIBH.setIBHE(cRefIBHEext.NO_ERRORS());
      APIBH.setIBLE(cRefIBLEext.NO_ERRORS());
      APIBH.setIBTP().moveLeftPad(cRefIBTPext.SUPPLIER_INVOICE());
      APIBH.setSUPA(cRefSUPAext.NEW());
      APIBH.setIMCD(cRefIMCDext.NO_PO_MATCHING());
   }

   /**
   * Sets parameters in step INITIATE of interaction maintain.
   */
   public void maintain_initiate_setParameters() {
      // Status invoice
      pMaintain.BIST.set(APIBH.getBIST());
      // Invoice head error
      pMaintain.IBHE.set(APIBH.getIBHE());
      // Invoice line error
      pMaintain.IBLE.set(APIBH.getIBLE());
      // Invoice batch type
      pMaintain.IBTP.set().moveLeftPad(APIBH.getIBTP());
      // Invoice status
      pMaintain.SUPA.set(APIBH.getSUPA());
      // Message
      if (!APIBH.getMSID().isBlank()) {
         COMRTM(APIBH.getMSID().toString(), "MVXMSG", APIBH.getMSGD().toString()); 	
         pMaintain.MSGD.set().moveLeftPad(SRCOMRCM.MSG);
      } else {
         pMaintain.MSGD.clearValue();
      }
      // Supplier invoice number
      pMaintain.SINO.set().moveLeftPad(APIBH.getSINO());
      // Payee
      pMaintain.SPYN.set().moveLeftPad(APIBH.getSPYN());
      // Supplier
      pMaintain.SUNO.set().moveLeftPad(APIBH.getSUNO());
      // Invoice date
      pMaintain.IVDT.set(APIBH.getIVDT());
      // Currency
      pMaintain.CUCD.set().moveLeftPad(APIBH.getCUCD());
      maintain_setCurrencyDecimals();
      // Exchange rate
      pMaintain.ARAT.set(APIBH.getARAT());
      // Payment terms
      pMaintain.TEPY.set().moveLeftPad(APIBH.getTEPY());
      // Payment method AP
      pMaintain.PYME.set().moveLeftPad(APIBH.getPYME());
      // Bank account identity
      pMaintain.BKID.set().moveLeftPad(APIBH.getBKID());
      // Trade code
      pMaintain.TDCD.set().moveLeftPad(APIBH.getTDCD());
      // Trade code description
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      found_CSYTAB_TDCD = cRefTDCDext.getCSYTAB_TDCD(SYTAB, false, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), pMaintain.TDCD.get());
      pMaintain.DSCR.set().moveLeftPad(SYTAB.getTX40());
      // Currency amount
      pMaintain.CUAM.set(APIBH.getCUAM());
      // VAT amount
      pMaintain.VTAM.set(APIBH.getVTAM());
      // Voucher number
      pMaintain.VONO.set(APIBH.getVONO());
      // Voucher number series
      pMaintain.VSER.set().moveLeftPad(APIBH.getVSER());
      // Accounting date
      pMaintain.ACDT.set(APIBH.getACDT());
      // Authorizer
      pMaintain.APCD.set().moveLeftPad(APIBH.getAPCD());
      // Invoice matching
      pMaintain.IMCD.set().move(APIBH.getIMCD());
      // Service code
      pMaintain.SERS.set(APIBH.getSERS());
      // Due date
      pMaintain.DUDT.set(APIBH.getDUDT());
      // Future exchange rate agreement
      pMaintain.FECN.set().moveLeftPad(APIBH.getFECN());
      // Currency rate type
      pMaintain.CRTP.set(APIBH.getCRTP());
      // From/To country
      pMaintain.FTCO.set().moveLeftPad(APIBH.getFTCO());
      // Base country
      pMaintain.BSCD.set().moveLeftPad(APIBH.getBSCD());
      // Total line amount
      pMaintain.TLNA.set(APIBH.getTLNA());
      // Total charges
      pMaintain.TCHG.set(APIBH.getTCHG());
      // Total due
      pMaintain.TOPA.set(APIBH.getTOPA());
      // Purchase order number
      pMaintain.PUNO.set().moveLeftPad(APIBH.getPUNO());
      // Order date
      pMaintain.PUDT.set(APIBH.getPUDT());
      // Cash discount terms
      pMaintain.TECD.set().moveLeftPad(APIBH.getTECD());
      // Cash discount date 1
      pMaintain.CDT1.set(APIBH.getCDT1());
      // Cash discount date 2
      pMaintain.CDT2.set(APIBH.getCDT2());
      // Cash discount date 3
      pMaintain.CDT3.set(APIBH.getCDT3());
      // Cash discount amount 1
      pMaintain.CDC1.set(APIBH.getCDC1());
      // Cash discount amount 2
      pMaintain.CDC2.set(APIBH.getCDC2());
      // Cash discount amount 3
      pMaintain.CDC3.set(APIBH.getCDC3());
      // Cash discount percentage 1
      pMaintain.CDP1.set(APIBH.getCDP1());
      // Cash discount percentage 2
      pMaintain.CDP2.set(APIBH.getCDP2());
      // Cash discount percentage 3
      pMaintain.CDP3.set(APIBH.getCDP3());
      // Cash discount base
      pMaintain.TASD.set(APIBH.getTASD());
      // Document code
      pMaintain.DNCO.set().moveLeftPad(APIBH.getDNCO());
      // Supplier acceptance
      pMaintain.SUAC.set(APIBH.getSUAC());
      // Conditions for adding lines
      pMaintain.SBAD.set(APIBH.getSBAD());
      // Invoice per receiving number
      pMaintain.UPBI.set(APIBH.getUPBI());
      // Invoic aggregation key 1
      pMaintain.IAK1.set().moveLeftPad(APIBH.getIAK1());
      // Invoic aggregation key 1
      pMaintain.IAK2.set().moveLeftPad(APIBH.getIAK2());
      // Invoic aggregation key 1
      pMaintain.IAK3.set().moveLeftPad(APIBH.getIAK3());
      // Invoic aggregation key 1
      pMaintain.IAK4.set().moveLeftPad(APIBH.getIAK4());
      // Invoic aggregation key 1
      pMaintain.IAK5.set().moveLeftPad(APIBH.getIAK5());
      // AP Standard document
      pMaintain.SDAP.set().moveLeftPad(APIBH.getSDAP());
      // Debit note reason
      pMaintain.DNRE.set().moveLeftPad(APIBH.getDNRE());
      // Our invoicing address
      pMaintain.PYAD.set().moveLeftPad(APIBH.getPYAD());
      // Text line 1
      pMaintain.SDA1.set().moveLeftPad(APIBH.getSDA1());
      // Text line 2
      pMaintain.SDA2.set().moveLeftPad(APIBH.getSDA2());
      // Text line 3
      pMaintain.SDA3.set().moveLeftPad(APIBH.getSDA3());
      // Original invoice number
      pMaintain.DNOI.set().moveLeftPad(APIBH.getDNOI());
      // Original invoice year
      pMaintain.OYEA.set(APIBH.getOYEA());
      // Payment reference number
      pMaintain.PPYR.set().moveLeftPad(APIBH.getPPYR());
      // Payment request number
      pMaintain.PPYN.set().moveLeftPad(APIBH.getPPYN());
      // Payment year
      pMaintain.YEA4.set(APIBH.getYEA4());
      // Total taxable amount
      pMaintain.TTXA.set(APIBH.getTTXA());
      // Pre-paid amount
      pMaintain.PRPA.set(APIBH.getPRPA());
      // VAT registration number
      pMaintain.VRNO.set().moveLeftPad(APIBH.getVRNO());
      // Tax applicable
      pMaintain.TXAP.set(APIBH.getTXAP());
      // Geographical code
      pMaintain.GEOC.set(APIBH.getGEOC());
      // Tax included 
      pMaintain.TXIN.set(APIBH.getTXIN());
      // EAN location code payee
      pMaintain.EALP.set().moveLeftPad(APIBH.getEALP());
      // EAN location code consignee
      pMaintain.EALR.set().moveLeftPad(APIBH.getEALR());
      // EAN location code supplier
      pMaintain.EALS.set().moveLeftPad(APIBH.getEALS());
      // Delivery date
      pMaintain.DEDA.set(APIBH.getDEDA());
      // Adjusted amount
      pMaintain.ADAB.set(APIBH.getADAB());
      // Approval date
      pMaintain.AAPD.set(APIBH.getAAPD());
      // Credit number
      pMaintain.CRNO.set().moveLeftPad(APIBH.getCRNO());
      // Your reference
      pMaintain.YRE1.set().moveLeftPad(APIBH.getYRE1());
      // Reject reason
      pMaintain.SCRE.set().moveLeftPad(APIBH.getSCRE());
      // Reprint after adjustment
      pMaintain.RPAA.set(APIBH.getRPAA());
      // Reject date
      pMaintain.REJD.set(APIBH.getREJD());
      // Entry date
      pMaintain.RGDT.set(APIBH.getRGDT());
      // Change date
      pMaintain.LMDT.set(APIBH.getLMDT());
      // Change ID
      pMaintain.CHID.set().moveLeftPad(APIBH.getCHID());
      // Text ID
      pMaintain.TXID.set(APIBH.getTXID());
      // Change number
      pMaintain.work_CHNO = APIBH.getCHNO();
      // Correlation ID
      pMaintain.CORI.set().moveLeftPad(APIBH.getCORI());
      // ------------------------
      // Save parameter values - to be able to later check if values changes
      pMaintain.work_SUNO.moveLeftPad(pMaintain.SUNO.get());
      pMaintain.work_SPYN.moveLeftPad(pMaintain.SPYN.get());
      pMaintain.work_SINO.moveLeftPad(pMaintain.SINO.get());
      pMaintain.work_PUNO.moveLeftPad(pMaintain.PUNO.get());
      pMaintain.work_TECD.moveLeftPad(pMaintain.TECD.get());
      pMaintain.work_IMCD.moveLeftPad(pMaintain.IMCD.get());
      pMaintain.work_BKID.moveLeftPad(pMaintain.BKID.get());
      // Check if main parameters have been set
      if (!pMaintain.IBTP.isBlank() &&
          !pMaintain.SPYN.isBlank() &&
          !pMaintain.SUNO.isBlank() &&
          !pMaintain.IVDT.isBlank() &&
          !pMaintain.IMCD.isBlank()) 
      {
         pMaintain.work_mainParamsSet = true;
      }
   }

   /**
   * Interaction maintain - step VALIDATE.
   */
   public void do_maintain_validate() {
      // Ensure notifications can be issued again (if the basic premises for the notifications has changed)
      if (pMaintain.SUNO.get().NE(pMaintain.work_SUNO) ||
          pMaintain.SINO.get().NE(pMaintain.work_SINO))
      {
         pMaintain.messages.forgetNotification("AP10018");
      }
      if (pMaintain.PUNO.get().NE(pMaintain.work_PUNO)) {
         pMaintain.messages.forgetNotification("AP10041");
         pMaintain.messages.forgetNotification("AP10057");
         pMaintain.messages.forgetNotification("AP10059");
      }
      if (pMaintain.PUNO.get().NE(pMaintain.work_PUNO) ||
          pMaintain.SUNO.get().NE(pMaintain.work_SUNO)) 
      {
         pMaintain.messages.forgetNotification("AP10046");
      }
      if (pMaintain.BKID.get().NE(pMaintain.work_BKID)) 
      {
         pMaintain.messages.forgetNotification("XBK1002");
      }
      if (pMaintain.SUNO.get().NE(pMaintain.work_SUNO) ||
          pMaintain.SINO.get().NE(pMaintain.work_SINO) ||
          pMaintain.DEDA.get() != pMaintain.work_DEDA) {
         pMaintain.messages.forgetNotification("X_00150");
      }
      // Perform validation
      maintain_validate();
      // Save parameter values - to be able to later check if values changes
      pMaintain.work_SUNO.moveLeftPad(pMaintain.SUNO.get());
      pMaintain.work_SPYN.moveLeftPad(pMaintain.SPYN.get());
      pMaintain.work_SINO.moveLeftPad(pMaintain.SINO.get());
      pMaintain.work_PUNO.moveLeftPad(pMaintain.PUNO.get());
      pMaintain.work_TECD.moveLeftPad(pMaintain.TECD.get());
      pMaintain.work_IMCD.moveLeftPad(pMaintain.IMCD.get());
      pMaintain.work_BKID.moveLeftPad(pMaintain.BKID.get());
      pMaintain.work_DEDA = pMaintain.DEDA.get();
   }

   /**
   * Interaction maintain - step VALIDATE.
   */
   public void maintain_validate() {
      int currentACYP = 0;
      boolean DIVIerror = false;
      cEnumAccessMode savedPUNOAccessMode = pMaintain.PUNO.getAccessMode();
      cEnumAccessMode savedPUDTAccessMode = pMaintain.PUDT.getAccessMode();
      cEnumAccessMode savedBKIDAccessMode = pMaintain.BKID.getAccessMode();
      // Validate main parameters
      // =========================================
      // Division
      if (pMaintain.DIVI.isAccessMANDATORYorOPTIONAL()) {
         if (!pMaintain.DIVI.validateMANDATORYandConstraints()) {
            DIVIerror = true;
         } else {
            if (!CRCommon.validateDIVIfinancialAccessAuthority(pMaintain.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
               pMaintain.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
               DIVIerror = true;
            }
         }
      }
      // Invoice batch type
      if (!DIVIerror) {
         setDynamicConstraintsForIBTP(pMaintain.DIVI.get());
      }
      maintain_validateIBTP();
      // Supplier
      maintain_validateSUNO();
      // Payee
      maintain_validateSPYN();
      // Invoice date
      validateIVDT(pMaintain.IVDT, pMaintain.DIVI.get(), pMaintain.partialVld.get(), pMaintain.messages, pMaintain);
      // Invoice matching
      maintain_validateIMCD();
      // Purchase order number
      maintain_validatePUNO();
      // Go no further if errors are found on main parameters
      if (pMaintain.messages.existError()) {
         return;
      }
      // Get default values for other parameters
      // =========================================
      maintain_validate_setDefaults();
      // Set Access mode (access modes might change due entered or defaulted values)
      // =========================================
      boolean mainParamsWereSetInStepValidate =
         pMaintain.getMode() == cEnumMode.ADD && !pMaintain.work_mainParamsSet;
      // All main parameters have been set and validated
      pMaintain.work_mainParamsSet = true;
      maintain_setAccessModeForEntry(); 
      if (mainParamsWereSetInStepValidate) {
         // Display the panel with new access modes and values
         pMaintain.setNewEntryContext();
      }
      if (pMaintain.isNewEntryContext()) {
         return;
      }
      // Validate other parameters
      // =========================================
      if (pMaintain.partialVld.get()) {
         maintain_roundAmounts();
         // No validation of other fields in ADD mode, since this is a batch entry program.
         return;
      }
      // Supplier invoice number
      boolean addMode = pMaintain.getMode() == cEnumMode.ADD;
      validateSINO(pMaintain.SINO, pMaintain.DIVI.get(), pMaintain.INBN.get(), pMaintain.SUNO.get(), pMaintain.IVDT.get(), addMode, pMaintain.messages);
      // Invoice status
      pMaintain.SUPA.validateMANDATORYandConstraints();
      // Currency
      pMaintain.CUCD.validateMANDATORYandConstraints();
      found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, pMaintain.CUCD.get());
      pMaintain.CUCD.validateExists(found_CSYTAB_CUCD);
      maintain_setCurrencyDecimals();
      maintain_roundAmounts();
      // Rate
      pMaintain.ARAT.validateMANDATORYandConstraints();
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.DIVI.get());
      if (MNDIV.getLOCD().EQ(pMaintain.CUCD.get()) &&
         !equals(pMaintain.ARAT.get(), pMaintain.ARAT.getDecimals(), 1d) &&
         !equals(pMaintain.ARAT.get(), pMaintain.ARAT.getDecimals(), 0d)) {
         // MSGID=WRA0701 Exchange rate &1 is invalid
         pMaintain.messages.addError(this.DSPGM, "ARAT", "WRA0701",
            CRCommon.formatNumForMsg(pMaintain.ARAT.get(), pMaintain.ARAT.getDecimals(), pMaintain.getDCFM()));
      }
      // Payment terms
      pMaintain.TEPY.validateMANDATORYandConstraints();
      found_CSYTAB_TEPY = cRefTEPYext.getCSYTAB_TEPY(SYTAB, found_CSYTAB_TEPY, currentCONO, pMaintain.TEPY.get(), IDMAS.getLNCD());
      pMaintain.TEPY.validateExists(found_CSYTAB_TEPY);
      // Payment method AP
      pMaintain.PYME.validateMANDATORYandConstraints();
      found_CSYTAB_PYME = cRefPYMEext.getCSYTAB_PYME(SYTAB, found_CSYTAB_PYME, currentCONO, pMaintain.PYME.get());
      pMaintain.PYME.validateExists(found_CSYTAB_PYME);
      // Check purchase order number. PO needed for country codes, hence this check is
      // moved before country codes
      if (!pMaintain.PUNO.isBlank() &&
          pMaintain.IMCD.get().EQ(cRefIMCDext.PO_HEAD_MATCHING())) 
      {
         found_MPHEAD = cRefPUNOext.getMPHEAD(PHEAD, found_MPHEAD, currentCONO, pMaintain.PUNO.get());
         //  Check if purchase header currency same as invoice
         if (found_MPHEAD && PHEAD.getCUCD().NE(pMaintain.CUCD.get())) {
            // MSGID=AP10057 WARNING - The purchase order has a different currency (&1) than the invoice
            pMaintain.messages.addNotification(this.DSPGM, "PUNO", "AP10057", PHEAD.getCUCD());
         }
      }
      if (savedPUNOAccessMode == cEnumAccessMode.DISABLED && pMaintain.PUNO.getAccessMode() != cEnumAccessMode.DISABLED ||
          savedPUNOAccessMode != cEnumAccessMode.DISABLED && pMaintain.PUNO.getAccessMode() == cEnumAccessMode.DISABLED) {
         pMaintain.setNewEntryContext();
      }
      // Bank account identity
      pMaintain.BKID.validateMANDATORYandConstraints();
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      if (!pMaintain.SPYN.isBlank()) {
         BANAC.setACHO().moveLeftPad(pMaintain.SPYN.get());
      } else {
         BANAC.setACHO().moveLeftPad(pMaintain.SUNO.get());
      }
      found_CBANAC = cRefBKIDext.getCBANAC(BANAC, found_CBANAC, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), 3, BANAC.getACHO(), pMaintain.BKID.get());
      pMaintain.BKID.validateExists(found_CBANAC);
      if (found_CBANAC) {
         if (BANAC.getSTAT().EQ("90")) {
            // MSGID=XBK1001 Bank account ID has status &1 (blocked/expired)
            pMaintain.messages.addError(this.DSPGM, "BKID", "XBK1001", BANAC.getSTAT());
         }
         if (BANAC.getSTAT().EQ("10")) {
            // MSGID=XBK1002 MSGID=XBK1002 Bank account ID has status &1 (preliminary)
            pMaintain.messages.addNotification(this.DSPGM, "BKID", "XBK1002", BANAC.getSTAT());
         }
      }
      if (savedBKIDAccessMode == cEnumAccessMode.DISABLED && pMaintain.BKID.getAccessMode() != cEnumAccessMode.DISABLED ||
          savedBKIDAccessMode != cEnumAccessMode.DISABLED && pMaintain.BKID.getAccessMode() == cEnumAccessMode.DISABLED) {
         pMaintain.setNewEntryContext();
      }
      // Check order date
      pMaintain.PUDT.validateMANDATORYandConstraints();
      if (!pMaintain.PUDT.isBlank()) {
         if (!CRCalendar.lookUpDate(currentCONO, pMaintain.DIVI.get(), pMaintain.PUDT.get())) {
            // MSGID=WPUD101 Order date &1 is invalid
            pMaintain.messages.addError(this.DSPGM, "PUDT", "WPUD101",
               CRCommon.formatDateForMsg(pMaintain.PUDT.get(), pMaintain.getDTFM(), pMaintain.getDSEP()));
         }

      }
      if (savedPUDTAccessMode == cEnumAccessMode.DISABLED && pMaintain.PUDT.getAccessMode() != cEnumAccessMode.DISABLED ||
          savedPUDTAccessMode != cEnumAccessMode.DISABLED && pMaintain.PUDT.getAccessMode() == cEnumAccessMode.DISABLED) {
         pMaintain.setNewEntryContext();
      }
      //  From/To country
      if (pMaintain.FTCO.getAccessMode() != cEnumAccessMode.DISABLED) {
         pMaintain.FTCO.validateMANDATORYandConstraints();
         found_CSYTAB_CSCD = cRefFTCOext.getCSYTAB_FTCO(SYTAB, found_CSYTAB_CSCD, currentCONO, pMaintain.FTCO.get());
         pMaintain.FTCO.validateExists(found_CSYTAB_CSCD);
      }
      //  Base country, trade code
      if (pMaintain.BSCD.getAccessMode() != cEnumAccessMode.DISABLED) {
         pMaintain.BSCD.validateMANDATORYandConstraints();
         found_CSYTAB_CSCD = cRefBSCDext.getCSYTAB_BSCD(SYTAB, found_CSYTAB_CSCD, currentCONO, pMaintain.BSCD.get());
         pMaintain.BSCD.validateExists(found_CSYTAB_CSCD);
      }
      // - Read APS905 - AP - parameters
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      found_CSYPAR_APS905 = cRefPGNMext.getCSYPAR_PGNM_DIVIorBlank(SYPAR, found_CSYPAR_APS905, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), "APS905");
      APS905DS.set().moveLeft(SYPAR.getPARM());
      if (APS905DS.getYXTDCU() != 0) {
         // Check trade code
         if (!pMaintain.BSCD.get().EQ(pMaintain.FTCO.get()) &&
             !pMaintain.BSCD.isBlank() &&
             pMaintain.TDCD.isBlank()) {
            // MSGID=WTDCD02 Trade code must be entered
            pMaintain.messages.addError(this.DSPGM, "TDCD", "WTDCD02");
         }
         found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.DIVI.get());
         found_CSYTAB_TDCD = cRefTDCDext.getCSYTAB_TDCD(SYTAB, found_CSYTAB_TDCD, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), pMaintain.TDCD.get());
         if (!found_CSYTAB_TDCD && !pMaintain.TDCD.isBlank()) {
            // MSGID=WTDCD03 Trade code &1 does not exist
            pMaintain.messages.addError(this.DSPGM, "TDCD", "WTDCD03", pMaintain.TDCD.get());
         }
         if (APS905DS.getYXTDCU() != 2) {
            if (found_CSYTAB_TDCD) {
               if (pMaintain.BSCD.get().EQ(pMaintain.FTCO.get()) &&
                   !pMaintain.BSCD.isBlank() ||
                   MNDIV.getCSCD().EQ(pMaintain.FTCO.get()) &&
                   pMaintain.BSCD.isBlank()) {
                  // MSGID=XAP0004 Supplier country code cannot be the same as the division
                  pMaintain.messages.addError(this.DSPGM, "FTCO", "XAP0004");
               }
            }
         }
         exitPointMarket_maintain_validate001();
      }
      // Currency amount
      pMaintain.CUAM.validateMANDATORYandConstraints();
      // VAT amount
      pMaintain.VTAM.validateMANDATORYandConstraints();
      // Accounting date
      pMaintain.ACDT.validateMANDATORYandConstraints();
      if (!pMaintain.ACDT.isBlank()) {
         if (!CRCalendar.lookUpDate(currentCONO, pMaintain.DIVI.get(), pMaintain.ACDT.get())) {
            // MSGID=WACD101 Invoice date &1 is invalid
            pMaintain.messages.addError(this.DSPGM, "ACDT", "WACD101",
               CRCommon.formatDateForMsg(pMaintain.ACDT.get(), pMaintain.getDTFM(), pMaintain.getDSEP()));
         }
         if (!isAccountingDateWithinLimits()) {
            // Error messages are added in test
         }
      } else {
         // ACDT should be set to zero
         pMaintain.ACDT.set(0);
      }
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.DIVI.get());
      if (CRCalendar.lookUpDate(currentCONO, pMaintain.DIVI.get(), pMaintain.ACDT.get())) {
         currentACYP = CRCalendar.getFiscalPeriod(MNDIV.getPTFA());
      } else {
         currentACYP = 0;
      }
      // Voucher number
      pMaintain.VONO.validateMANDATORYandConstraints();
      // Check Voucher number
      if (!pMaintain.VONO.isBlank()) {
         found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
         // If Automatic voucher number setting, high limit must be tested
         PLCHKVO.FVCONO = currentCONO;
         PLCHKVO.FVCMTP = MNCMP.getCMTP();
         PLCHKVO.FVDIVI.move(pMaintain.DIVI.get());
         if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
            PLCHKVO.FVFEID.move("AP53");
         } else if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) {
            PLCHKVO.FVFEID.move("AP54");
         } else {
            PLCHKVO.FVFEID.move("AP50");
         }
         // - Chain CRS750 - Finance parameters
         found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
         found_CSYPAR_CRS750 = cRefPGNMext.getCSYPAR_PGNM_DIVIorBlank(SYPAR, found_CSYPAR_CRS750, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), "CRS750");
         CRS750DS.set().moveLeft(SYPAR.getPARM());
         // Book of account used ?
         PLCHKVO.FVACBC = CRS750DS.getPBACBC();
         // VSER is an internal field and should be taken from Fam function
         getFamFunction();
         PLCHKVO.FVVSER.moveLeftPad(XXVSER);
         PLCHKVO.FVYEA4 = (int)(currentACYP/100);
         PLCHKVO.FVACDT = pMaintain.ACDT.get();
         PLCHKVO.FVVONI = pMaintain.VONO.get();
         PLCHKVO.FVVTST = 1;
         PLCHKVO.FVFETC = 0;
         IN92 = PLCHKVO.CCHKVON();
         if (PLCHKVO.FVVERR == 1) {
            // MSGID=message ID from CCHKVON
            pMaintain.messages.addError(this.DSPGM, "VONO", PLCHKVO.FVMSGI.toString(), PLCHKVO.FVMSGD);
         }
         // Test upper limit if automatic voucher number setting
         if (PLCHKVO.FVMVMA == 2) {
            PLCHKVO.FVCONO = currentCONO;
            PLCHKVO.FVCMTP = MNCMP.getCMTP();
            PLCHKVO.FVDIVI.move(pMaintain.DIVI.get());
            if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
               PLCHKVO.FVFEID.move("AP53");
            } else if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) {
               PLCHKVO.FVFEID.move("AP54");
            } else {
               PLCHKVO.FVFEID.move("AP50");
            }
            // Book of account used ?
            PLCHKVO.FVACBC = CRS750DS.getPBACBC();
            PLCHKVO.FVVSER.move(pMaintain.VSER.get());
            PLCHKVO.FVYEA4 = (int)(currentACYP/100);
            PLCHKVO.FVACDT = pMaintain.ACDT.get();
            PLCHKVO.FVVONI = 0;
            PLCHKVO.FVVTST = 1;
            PLCHKVO.FVFETC = 0;
            IN92 = PLCHKVO.CCHKVON();
            // Number over limit (error)
            if (PLCHKVO.FVVERR == 1) {
               //   MSGID=message ID from CCHKVON
               pMaintain.messages.addError(this.DSPGM, "VONO", PLCHKVO.FVMSGI.toString(), PLCHKVO.FVMSGD);
            }
            // Only 100 numbers remaining (Warning)
            if (!PLCHKVO.FVMSGI.isBlank()) {
               // MSGID=message ID from CCHKVON
               pMaintain.messages.addNotification(this.DSPGM, "VONO", PLCHKVO.FVMSGI.toString(), PLCHKVO.FVMSGD);
            }
         }
      }
      // Voucher number series
      pMaintain.VSER.validateMANDATORYandConstraints();
      // Authorized
      if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING()) || 	
          APIBH.getIBTP().EQ("00") || 	// old test for EDI invoice
          APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_INVOICE())) {
         found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
         getFamFunction();
         if (XXAPCH == 1 ||
            !pMaintain.APCD.isBlank()) {
            found_FAPRCD = cRefAPCDext.getFAPRCD(APRCD, found_FAPRCD, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), pMaintain.APCD.get());
            pMaintain.APCD.validateExists(found_FAPRCD);
            if (!isBlank(APRCD.getFRDT())) { 	
               if (movexDate() >= APRCD.getFRDT() && 	
                   movexDate() <= APRCD.getTODT()) { 	
                  if (!APRCD.getAPCG().isBlank()) { 	
                     // MSGID=AP05052 Authorizer has been changed - &1 is not allowed
                     pMaintain.messages.addError(this.DSPGM, "APCD", "AP05052", pMaintain.APCD.get());
                     pMaintain.APCD.set().moveLeftPad(APRCD.getAPCG());
                  } else { 	
                     // MSGID=AP05053 Authorizer &1 is not allowed
                     pMaintain.messages.addError(this.DSPGM, "APCD", "AP05053", pMaintain.APCD.get());
                  } 	
               } 	
            }
         }
      }
      // Service code
      pMaintain.SERS.validateMANDATORYandConstraints();
      found_CSYTAB_SERS = cRefSERSext.getCSYTAB_SERS(SYTAB, found_CSYTAB_SERS, currentCONO, pMaintain.SERS.get());
      pMaintain.SERS.validateExists(found_CSYTAB_SERS);
      // Invoice matching
      if (pMaintain.IMCD.get().EQ(cRefIMCDext.PO_LINE_MATCHING()) ||
          pMaintain.IMCD.get().EQ(cRefIMCDext.PO_HEAD_MATCHING())) 
      {
         found_MPHEAD = cRefPUNOext.getMPHEAD(PHEAD, found_MPHEAD, currentCONO, pMaintain.PUNO.get());
         found_MITWHL = cRefWHLOext.getMITWHL(ITWHL, found_MITWHL, currentCONO, PHEAD.getWHLO());
         if (found_MPHEAD && ITWHL.getCSCD().NE(pMaintain.BSCD.get())) {
            // MSGID=AP10059  WARNING - The purchase order has diffrent base contry &1 then the invoice 	
            pMaintain.messages.addNotification(this.DSPGM, "BSCD", "AP10059", ITWHL.getCSCD());
         }
      }
      // Due date
      validateDUDT(pMaintain.DUDT, pMaintain.DIVI.get(), pMaintain.messages, pMaintain);
      // Check future rate agreement
      if (!pMaintain.FECN.isBlank()) {
         found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
         PLCHKFE.FCCONO = currentCONO;
         PLCHKFE.FCDIVI.move(pMaintain.DIVI.get());
         PLCHKFE.FCPGNM.moveLeft("APS100");
         PLCHKFE.FCCMTP = MNCMP.getCMTP();
         PLCHKFE.FCFCTP = 2;
         PLCHKFE.FCFECN.move(pMaintain.FECN.get());
         PLCHKFE.FCCUCD.move(pMaintain.CUCD.get());
         PLCHKFE.FCIVDT = 0;
         PLCHKFE.FCTEPY.clear();
         PLCHKFE.FCDUDT = pMaintain.DUDT.get();
         PLCHKFE.FCCUAM = pMaintain.CUAM.get();
         PLCHKFE.FCARAT = 0d;
         IN92 = PLCHKFE.CCHKFEC();
         if (PLCHKFE.FCVERR == 1) {
            if (PLCHKFE.FCMSGA.isBlank()) {
               // MSGID=Error message from CCHKFEC
               pMaintain.messages.addError(this.DSPGM, "FECN", PLCHKFE.FCMSGI.toString(), CRCommon.formatNumForMsg(PLCHKFE.FCMSGN));
            } else {
               // MSGID=Error message from CCHKFEC
               pMaintain.messages.addError(this.DSPGM, "FECN", PLCHKFE.FCMSGI.toString(), PLCHKFE.FCMSGA);
            }
         }
      }
      // Exchange rate type
      pMaintain.CRTP.validateMANDATORYandConstraints();
      found_CSYTAB_CRTP = cRefCRTPext.getCSYTAB_CRTP(SYTAB, found_CSYTAB_CRTP, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), pMaintain.CRTP.get());
      pMaintain.CRTP.validateExists(found_CSYTAB_CRTP);
      // Cash discount terms
      pMaintain.TECD.validateMANDATORYandConstraints();
      found_CSYTAB_TECD = cRefTECDext.getCSYTAB_TECD(SYTAB, found_CSYTAB_TECD, currentCONO, pMaintain.TECD.get(), IDMAS.getLNCD());
      pMaintain.TECD.validateExists(found_CSYTAB_TECD);
      // Cash discount date 1
      pMaintain.CDT1.validateMANDATORYandConstraints();
      if (!pMaintain.CDT1.isBlank()) {
         if (!CRCalendar.lookUpDate(currentCONO, pMaintain.DIVI.get(), pMaintain.CDT1.get())) {
            // MSGID=WCD1001 Cash discount date 1 &1 is invalid
            pMaintain.messages.addError(this.DSPGM, "CDT1", "WCD1001",
               CRCommon.formatDateForMsg(pMaintain.CDT1.get(), pMaintain.getDTFM(), pMaintain.getDSEP()));
         }
      }
      // Cash discount date 2
      pMaintain.CDT2.validateMANDATORYandConstraints();
      if (!pMaintain.CDT2.isBlank()) {
         if (!CRCalendar.lookUpDate(currentCONO, pMaintain.DIVI.get(), pMaintain.CDT2.get())) {
            // MSGID=WCD1101 Cash discount date 2 &1 is invalid
            pMaintain.messages.addError(this.DSPGM, "CDT2", "WCD1101",
               CRCommon.formatDateForMsg(pMaintain.CDT2.get(), pMaintain.getDTFM(), pMaintain.getDSEP()));
         }
      }
      // Cash discount date 3
      pMaintain.CDT3.validateMANDATORYandConstraints();
      if (!pMaintain.CDT3.isBlank()) {
         if (!CRCalendar.lookUpDate(currentCONO, pMaintain.DIVI.get(), pMaintain.CDT3.get())) {
            // MSGID=WCD1201 Cash discount date 3 &1 is invalid
            pMaintain.messages.addError(this.DSPGM, "CDT3", "WCD1201",
               CRCommon.formatDateForMsg(pMaintain.CDT3.get(), pMaintain.getDTFM(), pMaintain.getDSEP()));
         }
      }
      // Cash discount amount 1
      pMaintain.CDC1.validateMANDATORYandConstraints();
      // Cash discount amount 2
      pMaintain.CDC2.validateMANDATORYandConstraints();
      // Cash discount amount 3
      pMaintain.CDC3.validateMANDATORYandConstraints();
      // Cash discount percentage 1
      pMaintain.CDP1.validateMANDATORYandConstraints();
      // Cash discount percentage 2
      pMaintain.CDP2.validateMANDATORYandConstraints();
      // Cash discount percentage 3
      pMaintain.CDP3.validateMANDATORYandConstraints();
      // Cash discount base
      pMaintain.TASD.validateMANDATORYandConstraints();
      // - Check cash discount date 1
      if (pMaintain.CDT1.isBlank() && !pMaintain.CDP1.isBlank() ||
          !pMaintain.CDT1.isBlank() && pMaintain.CDP1.isBlank()) 
      {
         // MSGID=XBP0001 Both percentage and date must be entered
         pMaintain.messages.addError(this.DSPGM, "CDT1", "XBP0001");
      }
      // - Check cash discount date 2
      if (pMaintain.CDT2.isBlank() && !pMaintain.CDP2.isBlank() ||
          !pMaintain.CDT2.isBlank() && pMaintain.CDP2.isBlank()) 
      {
         // MSGID=XBP0001 Both percentage and date must be entered
         pMaintain.messages.addError(this.DSPGM, "CDT2", "XBP0001");
      }
      // - Check cash discount date 3
      if (pMaintain.CDT3.isBlank() && !pMaintain.CDP3.isBlank() ||
          !pMaintain.CDT3.isBlank() && pMaintain.CDP3.isBlank()) 
      {
         // MSGID=XBP0001 Both percentage and date must be entered
         pMaintain.messages.addError(this.DSPGM, "CDT3", "XBP0001");
      }
      if (pMaintain.CDT1.isBlank() && !pMaintain.CDT2.isBlank() ||
          pMaintain.CDT1.isBlank() && !pMaintain.CDT3.isBlank() ||
          !pMaintain.CDT1.isBlank() && !pMaintain.CDT2.isBlank() && pMaintain.CDT2.get() <= pMaintain.CDT1.get()) 
      {
         // MSGID=MSGID=XDA0010 Dates must be entered in ascending order
         pMaintain.messages.addError(this.DSPGM, "CDT1", "XDA0010");
      }
      if (pMaintain.CDT2.isBlank() && !pMaintain.CDT3.isBlank() ||
          !pMaintain.CDT2.isBlank() && !pMaintain.CDT3.isBlank() && pMaintain.CDT3.get() <= pMaintain.CDT2.get()) 
      {
         // MSGID=MSGID=XDA0010 Dates must be entered in ascending order
         pMaintain.messages.addError(this.DSPGM, "CDT2", "XDA0010");
      }
      // Cash discount basis amount must be entered if % exist
      if (pMaintain.TASD.isBlank() && !pMaintain.CDP1.isBlank() ||
          pMaintain.TASD.isBlank() && !pMaintain.CDP2.isBlank() ||
          pMaintain.TASD.isBlank() && !pMaintain.CDP3.isBlank()) 
      {
         // MSGID=WCD0502 Cash discount bases amount must be entered
         pMaintain.messages.addError(this.DSPGM, "TASD", "WCD0502");
      }
      // Document code
      pMaintain.DNCO.validateMANDATORYandConstraints();
      // Supplier acceptance
      pMaintain.SUAC.validateMANDATORYandConstraints();
      // Conditions for adding lines
      pMaintain.SBAD.validateMANDATORYandConstraints();
      // Invoice per receiving number
      // - No need to validate UPBI since it is a cParamBoolean.
      // Invoice aggregation key 1
      pMaintain.IAK1.validateMANDATORYandConstraints();
      // Invoice aggregation key 2
      pMaintain.IAK2.validateMANDATORYandConstraints();
      // Invoice aggregation key 3
      pMaintain.IAK3.validateMANDATORYandConstraints();
      // Invoice aggregation key 4
      pMaintain.IAK4.validateMANDATORYandConstraints();
      // Invoice aggregation key 5
      pMaintain.IAK5.validateMANDATORYandConstraints();
      // AP Standard document
      pMaintain.SDAP.validateMANDATORYandConstraints();
      found_CSYTAB_SDAP = cRefSDAPext.getCSYTAB_SDAP(SYTAB, found_CSYTAB_SDAP, currentCONO, pMaintain.DIVI.get(), pMaintain.SDAP.get());
      pMaintain.SDAP.validateExists(found_CSYTAB_SDAP);
      // Debit note reason
      pMaintain.DNRE.validateMANDATORYandConstraints();
      found_CSYTAB_REAP = cRefREAPext.getCSYTAB_REAP(SYTAB, found_CSYTAB_REAP, currentCONO, pMaintain.DIVI.get(), pMaintain.DNRE.get());
      pMaintain.DNRE.validateExists(found_CSYTAB_REAP);
      // Our invoicing address
      pMaintain.PYAD.validateMANDATORYandConstraints();
      if (!pMaintain.PYAD.isBlank()) {
         IADDR.setADK1().clear();
         IADDR.setADK3().clear();
         found_CIADDR = cRefPYADext.getCIADDR(IADDR, found_CIADDR, currentCONO, 3, IADDR.getADK1(), pMaintain.PYAD.get(), IADDR.getADK3());
         pMaintain.PYAD.validateExists(found_CIADDR);
      }
      // Text line 1
      pMaintain.SDA1.validateMANDATORYandConstraints();
      // Text line 2
      pMaintain.SDA2.validateMANDATORYandConstraints();
      // Text line 3
      pMaintain.SDA3.validateMANDATORYandConstraints();
      // Total taxable amount
      pMaintain.TTXA.validateMANDATORYandConstraints();
      // Pre-paid amount
      pMaintain.PRPA.validateMANDATORYandConstraints();
      // VAT registration number
      pMaintain.VRNO.validateMANDATORYandConstraints();
      // Tax applicable
      pMaintain.TXAP.validateMANDATORYandConstraints();
      // Geographical code
      pMaintain.GEOC.validateMANDATORYandConstraints();
      found_CGEOJU = cRefGEOCext.getCGEOJU(GEOJU, found_CGEOJU, currentCONO, pMaintain.GEOC.get());
      pMaintain.GEOC.validateExists(found_CGEOJU);
      // Tax included
      // - No need to validate TXIN since it is a cParamBoolean.
      // EAN location code payee
      pMaintain.EALP.validateMANDATORYandConstraints();
      // EAN location code consignee
      pMaintain.EALR.validateMANDATORYandConstraints();
      // EAN location code supplier
      pMaintain.EALS.validateMANDATORYandConstraints();
      // Delivery date
      pMaintain.DEDA.validateMANDATORYandConstraints();
      if (pMaintain.DEDA.getAccessMode() != cEnumAccessMode.DISABLED) {
         // Only if VAT is used
         if (MNDIV.getTATM() == 1 || MNDIV.getTATM() == 4) {
            if (pMaintain.DEDA.isBlank()) {
               // MSGID=X_00150 WARNING - Delivery date has not been entered.
               pMaintain.messages.addNotification(this.DSPGM, "DEDA", "X_00150");            
            }
            if (!pMaintain.DEDA.isBlank()) {
               if (!CRCalendar.lookUpDate(currentCONO, pMaintain.DIVI.get(), pMaintain.DEDA.get())) {
                  // MSGID=WDEDA01 Delivery date &1 is invalid
                  pMaintain.messages.addError(this.DSPGM, "DEDA", "WDEDA01",
                     CRCommon.formatDateForMsg(pMaintain.DEDA.get(), pMaintain.getDTFM(), pMaintain.getDSEP()));
               }
            }
         }
      }   
      // Original invoice year
      pMaintain.OYEA.validateMANDATORYandConstraints();
      // Original invoice number
      maintain_validateDNOI();
      // Payment reference number
      pMaintain.PPYR.validateMANDATORYandConstraints();
      // Payment request number
      pMaintain.PPYN.validateMANDATORYandConstraints();
      // Payment year
      pMaintain.YEA4.validateMANDATORYandConstraints();
      // Validate Pre-payment data
      maintain_validatePrePayment();
      // CorrelationID
      pMaintain.CORI.validateMANDATORYandConstraints();
   }

   /**
    * Market modification Exit point 001 for method maintain_validate.
    */
   public void exitPointMarket_maintain_validate001() { 	 	
   } 	 	

  /**
   * Validate invoice batch type.
   * Messages are returned in pMaintain.messages.
   */
   public void maintain_validateIBTP() {
      if (pMaintain.partialVld.get()) {
         pMaintain.IBTP.validateMANDATORYandConstraints();
         return;
      }
      pMaintain.IBTP.validateMANDATORYandConstraints();
      // Invoice matching is not valid for Supplier claim
      if (pMaintain.IBTP.get().EQ(cRefIBTPext.SUPPLIER_CLAIM()) || 
          pMaintain.IBTP.get().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         // Invoice matching
         pMaintain.IMCD.set().move(cRefIMCDext.NO_PO_MATCHING());
      }
   }

   /**
   * Dynamically sets allowed values for IBTP based on setup in CRS750.
   * @param DIVI
   *    Division
   */
   public void setDynamicConstraintsForIBTP(MvxString DIVI) {
      // Chain CRS750 - Finance parameters
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      found_CSYPAR_CRS750 = cRefPGNMext.getCSYPAR_PGNM_DIVIorBlank(SYPAR, found_CSYPAR_CRS750, MNCMP.getCMTP(), currentCONO, DIVI, "CRS750");
      CRS750DS.set().moveLeft(SYPAR.getPARM());
      // Return value map
      if (toBoolean(CRS750DS.getPBPRPY())) {
         // Prepayment active - set value map from reference field IBTP
         pMaintain.IBTP.setValueMap(cRefIBTP.constraints().getValueMap());
      } else {
         // Prepayment disabled
         pMaintain.IBTP.setValueMap(cRefIBTPext.valueMapWithoutPrepayment());
      }
   }

   /**
   * Validate supplier.
   * Also, sets pMaintain.SPYN (payee) from SUNO if SPYN is blank.
   * Messages are returned in pMaintain.messages.
   */
   public void maintain_validateSUNO() {
      if (pMaintain.partialVld.get()) {
         pMaintain.SUNO.validateMANDATORY();
      } else {
         pMaintain.SUNO.validateMANDATORYandConstraints();
         found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, found_CIDMAS, currentCONO, pMaintain.SUNO.get());
         pMaintain.SUNO.validateExists(found_CIDMAS);
         if (found_CIDMAS) {
            if (IDMAS.getSUTY() == 2) {
               // This supplier is only valid as Payee (SPYN) 
               // MSGID=XSU0002 Supplier not permitted - supplier type &1
               pMaintain.messages.addError(this.DSPGM, "SUNO", "XSU0002", CRCommon.formatNumForMsg(IDMAS.getSUTY()));
            }
         }
      }
      // Set default value for payee if payee has not been set (only in ADD-mode)
      if (pMaintain.SPYN.isBlank() && pMaintain.getMode() == cEnumMode.ADD && 
          !pMaintain.messages.existError()) 
      {
         foundParam_CIDVEN.moveLeft(toChar(found_CIDVEN));
         foundParam_CSUDIV.moveLeft(toChar(found_CSUDIV));
         cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, currentCONO, pMaintain.DIVI.get(),  pMaintain.SUNO.get());
         found_CIDVEN = foundParam_CIDVEN.getBoolean();
         found_CSUDIV = foundParam_CSUDIV.getBoolean();
         if (!IDVEN.getPRSU().isBlank()) {
            pMaintain.SPYN.set().moveLeftPad(IDVEN.getPRSU());
         } else {
            pMaintain.SPYN.set().moveLeftPad(pMaintain.SUNO.get());
         }
      }
   }

   /**
   * Validate payee.
   * Messages are returned in pMaintain.messages.
   */
   public void maintain_validateSPYN() {
      if (pMaintain.partialVld.get()) {
         pMaintain.SPYN.validateMANDATORY();
         return;
      }
      pMaintain.SPYN.validateMANDATORYandConstraints();
      found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, found_CIDMAS, currentCONO, pMaintain.SPYN.get());
      pMaintain.SPYN.validateExists(found_CIDMAS);
      if (!pMaintain.SPYN.isBlank() && found_CIDMAS) {
         if (IDMAS.getSTAT().NE("20")) {
            // MSGID=XSU0001 Supplier not permitted - status is &1
            pMaintain.messages.addError(this.DSPGM, "SPYN", "XSU0001", IDMAS.getSTAT());
         }
      }
   }

   /**
   * Validate invoice date.
   * @param IVDT
   *     Invoice date to validate
   * @param DIVI
   *     Division
   * @param partialValidate
   *     True if invoice date should only be partially validated
   * @param messages
   *     Returns error messages or notifications
   * @param plist
   *     A reference to the current parameter list
   */
   public void validateIVDT(cParamInt IVDT, MvxString DIVI, boolean partialValidate, cCRMessageList messages, GenericParm2 plist) {
      IVDT.validateMANDATORYandConstraints();
      if (partialValidate) {
         return;
      }
      if (!IVDT.isBlank()) {
         if (!CRCalendar.lookUpDate(currentCONO, DIVI, IVDT.get())) {
            // MSGID=WIVD101 Invoice date &1 is invalid
            messages.addError(this.DSPGM, "IVDT", "WIVD101",
               CRCommon.formatDateForMsg(IVDT.get(), plist.getDTFM(), plist.getDSEP()));
         }
      }
   }

   /**
   * Validate supplier invoice number.
   * @param SINO
   *     Supplier invoice number to check
   * @param DIVI
   *     Division
   * @param INBN
   *     Invoice batch number
   * @param SUNO
   *     Supplier
   * @param IVDT
   *     Invoice date
   * @param addMode
   *     True if called in add mode
   * @param messages
   *     Returns error messages or notifications
   */
   public void validateSINO(cParamMvxString SINO, MvxString DIVI, long INBN, MvxString SUNO, int IVDT, boolean addMode, cCRMessageList messages) {
      SINO.validateMANDATORYandConstraints();
      found_FCR040 = cRefSINOext.getSINOinFCR040(CR040, currentCONO, DIVI, SUNO, SINO.get());
      if (found_FCR040) {
         // MSGID=WSI1704 Supplier invoice number &1 already exists
         messages.addError(this.DSPGM, "SINO", "WSI1704", SINO.get());
      }
      if (!found_FCR040) {
         FieldSelection fieldSelection = cRefSINOext.setFieldSelection_40();
         found_FPLEDG = cRefSINOext.getFPLEDG_40_5(PLEDG, fieldSelection, currentCONO, DIVI, SUNO, SINO.get(), getYearFromDate(IVDT, DIVI));
         if (found_FPLEDG) {
            // MSGID=WSI1704 Supplier invoice number &1 already exists
            messages.addError(this.DSPGM, "SINO", "WSI1704", SINO.get());
         }
         if (!found_FPLEDG) {
            // Check if Invoice number exist for a other year in FPLEDG
            found_FPLEDG = cRefSINOext.getFPLEDG_40_4(PLEDG, fieldSelection, currentCONO, DIVI, SUNO, SINO.get());
            if (found_FPLEDG) {
               // MSGID=AP10018 WARNING - Supplier invoice &1 already exists for previou
               messages.addNotification(this.DSPGM, "SINO", "AP10018", SINO.get());
            }
            // Check if already registred in FAPIBH
            if (saved_APIBH == null) {
               // Init saved_APIBH to set the length
               saved_APIBH = APIBH.getEmptyRecord();
            }
            saved_APIBH.setRecord(APIBH);
            APIBH.setCONO(currentCONO);
            APIBH.setDIVI().moveLeftPad(DIVI);
            APIBH.setSINO().moveLeftPad(SINO.get());
            if (addMode) {
               if (APIBH.CHAIN("10", APIBH.getKey("10", 3))) {
                  // MSGID=WSI1704 Supplier invoice number &1 already exists
                  messages.addError(this.DSPGM, "SINO", "WSI1704", SINO.get());
               }
            } else {
               if (IVDT != 0){
                  APIBH.SETLL("10", APIBH.getKey("10", 3));
                  while (APIBH.READE("10", APIBH.getKey("10", 3))) {
                     if (APIBH.getINBN() != INBN &&
                         APIBH.getSUNO().EQ(SUNO) &&
                         getYearFromDate(APIBH.getIVDT(), DIVI) == getYearFromDate(IVDT, DIVI)) {
                        // MSGID=WSI1704 Supplier invoice number &1 already exists
                        messages.addError(this.DSPGM, "SINO", "WSI1704", SINO.get());
                     } else {
                        if (APIBH.getINBN() != INBN &&
                            APIBH.getSUNO().EQ(SUNO)) {
                           // MSGID=AP10018 WARNING - Supplier invoice &1 already exists for previou
                           messages.addNotification(this.DSPGM, "SINO", "AP10018", SINO.get());
                        }
                     }
                  }
               }
            }
            // Restore currenct FAPIBH record information
            APIBH.setRecord(saved_APIBH);
         }
      }
   }

  /**
   * Validate original invoice number.
   * Messages are returned in pMaintain.messages.
   */
   public void maintain_validateDNOI() {
      pMaintain.DNOI.validateMANDATORYandConstraints();
      if (!pMaintain.DNOI.isBlank()) {
         if (pMaintain.OYEA.isBlank()) {
            // MSGID=WOYEA02 Original invoice year must be entered
            pMaintain.messages.addError(this.DSPGM, "OYEA", "WOYEA02");
         }
         FieldSelection fieldSelection = cRefSINOext.setFieldSelection_40();
         found_FPLEDG = cRefSINOext.getFPLEDG_40_5(PLEDG, fieldSelection, currentCONO, pMaintain.DIVI.get(), pMaintain.SUNO.get(), pMaintain.DNOI.get(), pMaintain.OYEA.get());
         if (!found_FPLEDG) {
            // MSGID=WDN2703 Original invoice number &1 does not exist 	
            pMaintain.messages.addError(this.DSPGM, "DNOI", "WDN2703", pMaintain.DNOI.get());
         }
      }
   }

   /**
   * Validate purchase order number.
   * Messages are returned in pMaintain.messages.
   */
   public void maintain_validatePUNO() {
      if (pMaintain.partialVld.get()) {
         return;
      }
      // Check purchase order number. PO needed for country codes, hence this check is
      // moved before country codes
      if (!pMaintain.PUNO.isBlank()) 
      {
         if (pMaintain.IMCD.get().EQ(cRefIMCDext.PO_LINE_MATCHING()) ||
             pMaintain.IMCD.get().EQ(cRefIMCDext.NO_PO_MATCHING())) {
            // MSGID=X_01010 Purchase order number must be blank when invoice matching is &1
            pMaintain.messages.addError(this.DSPGM, "PUNO", "X_01010", pMaintain.IMCD.formatForMsg());
            return;
         }
         if (pMaintain.IMCD.get().EQ(cRefIMCDext.PO_HEAD_MATCHING())) {
            found_MPHEAD = cRefPUNOext.getMPHEAD(PHEAD, false, currentCONO, pMaintain.PUNO.get());
            pMaintain.PUNO.validateExists(found_MPHEAD);
            if (found_MPHEAD) {
               // Check status
               if (PHEAD.getPUSL().EQ("99")) {
                  // MSGID=AP10041 The purchase order has status &1
                  pMaintain.messages.addError(this.DSPGM, "PUNO", "AP10041", PHEAD.getPUSL());
               }
               if (PHEAD.getSUNO().NE(pMaintain.SUNO.get())) {
                  // MSGID=AP10046 WARNING - PO &2 has a different supplier (&1) than the invoice
                  messageData_AP10046_SUNO.moveLeftPad(PHEAD.getSUNO());
                  messageData_AP10046_PUNO.moveLeftPad(PHEAD.getPUNO());
                  pMaintain.messages.addNotification(this.DSPGM, "PUNO", "AP10046", messageData_AP10046);
               }
               // No check if Debit note
               if (APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE())) {
                  if (PHEAD.getPUSL().EQ("85")) {
                     // MSGID=AP10041 The purchase order has status &1
                     pMaintain.messages.addNotification(this.DSPGM, "PUNO", "AP10041", PHEAD.getPUSL());
                  }
               }
               if (CRCommon.isCentralUser() ||
                   !pMaintain.DIVI.isBlank()) {
                  found_CFACIL = cRefFACIext.getCFACIL(FACIL, found_CFACIL, currentCONO, PHEAD.getFACI());
                  if (found_CFACIL &&
                      FACIL.getDIVI().NE(pMaintain.DIVI.get())) {
                  // MSGID=AP35503 The facility where the purchase order originated belongs to division &1
                  pMaintain.messages.addError(this.DSPGM, "PUNO", "AP35503", FACIL.getDIVI());
                  }
               }
            }
         }
      } else {
         if (pMaintain.IMCD.get().EQ(cRefIMCDext.PO_HEAD_MATCHING())) {
            // MSGID=WPU0802 Purchase order number must be entered
            pMaintain.messages.addError(this.DSPGM, "PUNO", "WPU0802");
         }
      }
   }

  /**
   * Validate purchase invoice matching.
   * Messages are returned in pMaintain.messages.
   */
   public void maintain_validateIMCD() {
      // Invoice matching
      pMaintain.IMCD.validateMANDATORYandConstraints();
   }

   /**
   * Validate due date.
   * @param DUDT
   *     Invoice date to validate
   * @param DIVI
   *     Division
   * @param messages
   *     Returns error messages or notifications
   * @param plist
   *     A reference to the current parameter list
   */
   public void validateDUDT(cParamInt DUDT, MvxString DIVI, cCRMessageList messages, GenericParm2 plist) {
      DUDT.validateMANDATORYandConstraints();
      if (!DUDT.isBlank()) {
         if (!CRCalendar.lookUpDate(currentCONO, DIVI, DUDT.get())) {
            // MSGID=WDUD101 Due date &1 is invalid
            messages.addError(this.DSPGM, "DUDT", "WDUD101",
               CRCommon.formatDateForMsg(DUDT.get(), plist.getDTFM(), plist.getDSEP()));
         }
      }
   }

  /**
   * Validate pre-payment
   * Messages are returned in pMaintain.messages.
   */
   public void maintain_validatePrePayment() {
      boolean PPYRInSameCurrencyExists = false;
      if (pMaintain.IBTP.get().NE(cRefIBTPext.PREPAYMENT_PREINVOICE()) && 
          pMaintain.IBTP.get().NE(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) {
         return;
      }
      //   Retrieve voucher series no
      if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
         value_FEID.move("AP53");
         value_FNCN.moveLeft(1, cRefFNCN.length());
      }
      if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) {
         value_FEID.move("AP54");
         value_FNCN.moveLeft(1, cRefFNCN.length());
      }
      value_FFNC.moveLeftPad(value_FEID);
      value_FFNC.move(value_FNCN);
      SYTAB.setSTKY().moveLeftPad(value_FFNC);
      found_CSYTAB_FFNC = cRefFFNCext.getCSYTAB_FFNC(SYTAB, found_CSYTAB_FFNC, currentCONO, pMaintain.DIVI.get(), SYTAB.getSTKY());
      cRefFFNCext.setDSFFNC(SYTAB, DSFFNC);
      if (!found_CSYTAB_FFNC) {
         // MSGID=AR31103 FAM function &1 does not exists 	
         pMaintain.messages.addError(this.DSPGM, "PPYR", "AR31103", value_FNCN);
      } else {
         XXVSER.moveLeft(DSFFNC.getDFVSER());
      }
      // Check reference number 
      if (pMaintain.PPYR.isBlank()) {
         // MSGID=WPPYR02 Reference number must be entered
         pMaintain.messages.addError(this.DSPGM, "PPYR", "WPPYR02");
         // There is no use of testing more if PPYR is missing
         return;
      }else{
         // Check reference number	
         PPPAY.setCONO(currentCONO);
         PPPAY.setDIVI().moveLeftPad(pMaintain.DIVI.get());
         PPPAY.setPPYT().moveLeftPad("01");
         PPPAY.setSUNO().moveLeftPad(pMaintain.SUNO.get());
         if (pMaintain.SPYN.isBlank()) {
            PPPAY.setSPYN().moveLeftPad(pMaintain.SUNO.get());
         } else {
            PPPAY.setSPYN().moveLeftPad(pMaintain.SPYN.get());
         }         
         PPPAY.setPPYR().moveLeftPad(pMaintain.PPYR.get());
         if (!PPPAY.CHAIN("80", PPPAY.getKey("80", 6))) { 	
            // MSGID=WPPYR03 Reference number &1 does not exist
            pMaintain.messages.addError(this.DSPGM, "PPYR", "WPPYR03", pMaintain.PPYR.get());
            // There is no use of testing more if PPYR is missing
            return;
         }
      }
      // Check request number and year if prepayment 
      if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) { 
         if (!pMaintain.PPYN.isBlank() || !pMaintain.YEA4.isBlank()) {
            if (!pMaintain.PPYN.isBlank() && pMaintain.YEA4.isBlank()) {
               // MSGID=WYEA102 Year must be entered
               pMaintain.messages.addError(this.DSPGM, "YEA4", "WYEA102");
            }
            if (pMaintain.PPYN.isBlank() && !pMaintain.YEA4.isBlank()) {
               // MSGID=WPPYN02 Payment request number must be entered
               pMaintain.messages.addError(this.DSPGM, "PPYN", "WPPYN02");
            }
            if (!pMaintain.PPYN.isBlank() && !pMaintain.YEA4.isBlank()) {
               // Check referencenumber with requestnumber and year	
               PPPAY.setCONO(currentCONO);
               PPPAY.setDIVI().moveLeftPad(pMaintain.DIVI.get());
               PPPAY.setPPYT().moveLeftPad("01");
               PPPAY.setSUNO().moveLeftPad(pMaintain.SUNO.get());
               PPPAY.setPPYR().moveLeftPad(pMaintain.PPYR.get());
               PPPAY.setYEA4(pMaintain.YEA4.get());
               PPPAY.setPPYN().moveLeftPad(pMaintain.PPYN.get());
               if (pMaintain.SPYN.isBlank()) {
                  PPPAY.setSPYN().moveLeftPad(pMaintain.SUNO.get());
               } else {
                  PPPAY.setSPYN().moveLeftPad(pMaintain.SPYN.get());
               } 
               if (!PPPAY.CHAIN("70", PPPAY.getKey("70", 8))) { 	
                  // MSGID=WPPYN03 Request number &1 does not exist
                  pMaintain.messages.addError(this.DSPGM, "PPYN", "WPPYN03", pMaintain.PPYN.get());
               } else {
                  // Must be paid
                  if (PPPAY.getPPYS() != 6) {
                     //   MSGID=AP10069 Requestnumber (&1) has wrong status.
                     pMaintain.messages.addError(this.DSPGM, "PPYN", "AP10069", pMaintain.PPYN.get());
                  } else {
                     if (!equals(PPPAY.getCUAM(), cRefCUAM.decimals(), pMaintain.CUAM.get())) {
                        //   MSGID=AP10063 Entered amount is not equal to prepayed amount.
                        pMaintain.messages.addError(this.DSPGM, "CUAM", "AP10063");
                     } else {
                        if (PPPAY.getCUCD().NE(pMaintain.CUCD.get())) {
                           //   MSGID=AP10064 WARNING - Entered currency is not equal to prepaid currency (&1)
                           pMaintain.messages.addNotification(this.DSPGM, "CUCD", "AP10064", PPPAY.getCUCD());
                        }
                     }
                  }
               }
            }
         } else {
            // Check referencenumber (Requestnumber and year not entered)	
            PPPAY.setCONO(currentCONO);
            PPPAY.setDIVI().moveLeftPad(pMaintain.DIVI.get());
            PPPAY.setPPYT().moveLeftPad("01");
            PPPAY.setSUNO().moveLeftPad(pMaintain.SUNO.get());
            if (pMaintain.SPYN.isBlank()) {
               PPPAY.setSPYN().moveLeftPad(pMaintain.SUNO.get());
            } else {
               PPPAY.setSPYN().moveLeftPad(pMaintain.SPYN.get());
            } 
            PPPAY.setPPYR().moveLeftPad(pMaintain.PPYR.get());
            if (!PPPAY.CHAIN("80", PPPAY.getKey("80", 6))) { 	
               // MSGID=WPPYR03 Reference number &1 does not exist
               pMaintain.messages.addError(this.DSPGM, "PPYR", "WPPYR03", pMaintain.PPYR.get());
            }
         }
      }
      // Check referencenumber if final invoice
      if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) { 
         PPPAY.setCONO(currentCONO);
         PPPAY.setDIVI().moveLeftPad(pMaintain.DIVI.get());
         PPPAY.setPPYT().moveLeftPad("01");
         PPPAY.setSUNO().moveLeftPad(pMaintain.SUNO.get());
         if (pMaintain.SPYN.isBlank()) {
            PPPAY.setSPYN().moveLeftPad(pMaintain.SUNO.get());
         } else {
            PPPAY.setSPYN().moveLeftPad(pMaintain.SPYN.get());
         } 
         PPPAY.setPPYR().moveLeftPad(pMaintain.PPYR.get());
         PPPAY.setCUCD().clear();
         PPPAY.setYEA4(0);
         PPPAY.setPPYN().clear();
         PPPAY.SETLL("80", PPPAY.getKey("80", 9));
         while (PPPAY.READE("80", PPPAY.getKey("80", 6))) {                
            if (PPPAY.getCUCD().EQ(pMaintain.CUCD.get())) {
               PPYRInSameCurrencyExists = true;
               break;
            }
         }
         if (!PPYRInSameCurrencyExists) { 	
            //   MSGID=AP10066 WARNING - Entered currency (&1) does not exist for actual referencenumber
            pMaintain.messages.addNotification(this.DSPGM, "CUCD", "AP10066", pMaintain.CUCD.get());
         }
      }
   }

   /**
   * Sets default values for parameters in step VALIDATE.
   */
   public void maintain_validate_setDefaults() {
      // Set default values
      // -------------------------------
      // Make sure payee defaults are retrieved only once for automatic caller.
      if (pMaintain.isAutomated() && pMaintain.work_payeeDefaults) {
         // GPDF is disabled, since an automatic caller might keep asking for defaults. Only retrieve defaults once if automated.
         pMaintain.GPDF.set(false);
      }
      // Currency
      if (pMaintain.GPDF.get() && pMaintain.CUCD.get().isBlank()) {
         foundParam_CIDVEN.moveLeft(toChar(found_CIDVEN));
         foundParam_CSUDIV.moveLeft(toChar(found_CSUDIV));
         cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, currentCONO, pMaintain.DIVI.get(),  pMaintain.SUNO.get());
         found_CIDVEN = foundParam_CIDVEN.getBoolean();
         found_CSUDIV = foundParam_CSUDIV.getBoolean();
         pMaintain.CUCD.set().moveLeftPad(IDVEN.getCUCD());
         pMaintain.setNewEntryContext();
      }
      found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, pMaintain.CUCD.get());
      maintain_setCurrencyDecimals();
      // Geographical code 
      if (pMaintain.GPDF.get()) {
         found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.DIVI.get());         
         // Sales tax
         if (MNDIV.getTATM() == 2) {
            // If not Invoice matching
            if (pMaintain.IMCD.get().EQ(cRefIMCDext.NO_PO_MATCHING())) {
               pMaintain.GEOC.set(MNDIV.getGEOC());
               // Correct access for next display will be set in ) when all fields are opened up
               pMaintain.setNewEntryContext();   
            }
         }
      }
      // Payment terms
      if (pMaintain.GPDF.get()) {
         foundParam_CIDVEN.moveLeft(toChar(found_CIDVEN));
         foundParam_CSUDIV.moveLeft(toChar(found_CSUDIV));
         cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, currentCONO, pMaintain.DIVI.get(),  pMaintain.SPYN.get());
         found_CIDVEN = foundParam_CIDVEN.getBoolean();
         found_CSUDIV = foundParam_CSUDIV.getBoolean();
         pMaintain.TEPY.set().moveLeftPad(IDVEN.getTEPY());
         // Terms of cash discount
         pMaintain.TECD.set().moveLeftPad(IDVEN.getTECD());
         // Payment method AP
         pMaintain.PYME.set().moveLeftPad(IDVEN.getPYME());
         pMaintain.setNewEntryContext();
      }
      if (pMaintain.GPDF.get()) {
         if (!pMaintain.PUNO.isBlank()) {
            found_MPHEAD = cRefPUNOext.getMPHEAD(PHEAD, found_MPHEAD, currentCONO, pMaintain.PUNO.get());
            // Override with payment terms from Purchase order 
            if (found_MPHEAD &&
               pMaintain.getMode() == cEnumMode.ADD &&
               PHEAD.getSUNO().EQ(pMaintain.SUNO.get()) &&
               !PHEAD.getTEPY().isBlank() &&
               PHEAD.getTEPY().NE(pMaintain.TEPY.get())) {
               pMaintain.TEPY.set().moveLeftPad(PHEAD.getTEPY());
               // Display the panel with new payment method
               pMaintain.setNewEntryContext();
            }
            // Override with payment method from Purchase order 
            if (found_MPHEAD &&
               pMaintain.getMode() == cEnumMode.ADD &&
               PHEAD.getSUNO().EQ(pMaintain.SUNO.get()) &&
               !PHEAD.getPYME().isBlank() &&
               PHEAD.getPYME().NE(pMaintain.PYME.get())) {
               pMaintain.PYME.set().moveLeftPad(PHEAD.getPYME());
               // Display the panel with new payment method
               pMaintain.setNewEntryContext();
            }
         }
      }
      // Bank account identity
      if (pMaintain.GPDF.get()) {
         if (!pMaintain.SPYN.isBlank()) {
            pMaintain.BKID.set().moveLeftPad(getBankId(pMaintain.DIVI.get(), pMaintain.SPYN.get()));
         } else {
            pMaintain.BKID.set().moveLeftPad(getBankId(pMaintain.DIVI.get(), pMaintain.SUNO.get()));
         }
         if (!pMaintain.BKID.isBlank()) {
            pMaintain.setNewEntryContext();
         }
      }
      // Trade code
      if (pMaintain.GPDF.get()) {
         foundParam_CIDVEN.moveLeft(toChar(found_CIDVEN));
         foundParam_CSUDIV.moveLeft(toChar(found_CSUDIV));
         cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, currentCONO, pMaintain.DIVI.get(),  pMaintain.SPYN.get());
         found_CIDVEN = foundParam_CIDVEN.getBoolean();
         found_CSUDIV = foundParam_CSUDIV.getBoolean();
         pMaintain.TDCD.set().moveLeftPad(IDVEN.getTDCD());
         pMaintain.setNewEntryContext();
      }
      // Trade code description
      if (pMaintain.GPDF.get()) {
         found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
         found_CSYTAB_TDCD = cRefTDCDext.getCSYTAB_TDCD(SYTAB, false, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), pMaintain.TDCD.get());
         if (pMaintain.DSCR.get().NE(SYTAB.getTX40())) {
            pMaintain.DSCR.set().moveLeftPad(SYTAB.getTX40());
            pMaintain.setNewEntryContext();
         }
      }
      // Authorized user
      if (pMaintain.GPDF.get()) {
         if (pMaintain.APCD.isBlank() ||
             pMaintain.GPDF.get()) {
            getFamFunction();
            if (XXAPCH == 2) {
               if (!pMaintain.PUNO.isBlank()) {
                  found_MPHEAD = cRefPUNOext.getMPHEAD(PHEAD, found_MPHEAD, currentCONO, pMaintain.PUNO.get());
                  if (found_MPHEAD &&
                      !PHEAD.getBUYE().isBlank()) {
                     pMaintain.APCD.set().moveLeftPad(PHEAD.getBUYE());
                     pMaintain.setNewEntryContext();
                  }
               }
            }
            if (pMaintain.APCD.isBlank() ||
                pMaintain.GPDF.get()) {
               foundParam_CIDVEN.moveLeft(toChar(found_CIDVEN));
               foundParam_CSUDIV.moveLeft(toChar(found_CSUDIV));
               cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, currentCONO, pMaintain.DIVI.get(),  pMaintain.SPYN.get());
               found_CIDVEN = foundParam_CIDVEN.getBoolean();
               found_CSUDIV = foundParam_CSUDIV.getBoolean();
               pMaintain.APCD.set().moveLeftPad(IDVEN.getRESP());
               pMaintain.setNewEntryContext();
            }
         }
      }
      // Base Country / From To Country
      if (pMaintain.PUNO.get().NE(pMaintain.work_PUNO) ||
          pMaintain.FTCO.isBlank() && pMaintain.BSCD.isBlank() ||
          pMaintain.GPDF.get()) 
      {
         if (pMaintain.PUNO.isBlank()) {
            // FTCO must be set first
            if (pMaintain.FTCO.isBlank() ||
                pMaintain.GPDF.get()) {
               setFromToCountry(pMaintain.PUNO.get());
            }
            if (pMaintain.BSCD.isBlank() ||
                pMaintain.GPDF.get()) {
               setBaseCountry(pMaintain.PUNO.get());
            }
         } else {
            // BSCD must be set first
            if (pMaintain.BSCD.isBlank() ||
                pMaintain.GPDF.get()) {
               setBaseCountry(pMaintain.PUNO.get());
            }
            if (pMaintain.FTCO.isBlank() ||
                pMaintain.GPDF.get()) {
               setFromToCountry(pMaintain.PUNO.get());
            }
         }
         pMaintain.setNewEntryContext();
      }
      // Accounting date
      if (pMaintain.ACDT.isBlank() && !pMaintain.VONO.isBlank()) {
         // ACDT should be initiated with todays date if blank when VONO is entered
         pMaintain.ACDT.set(movexDate());
         // Make sure the panel is displayed again
         pMaintain.setNewEntryContext();
      }
      // Voucher number series
      // Chain CRS750 - Finance parameters
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      found_CSYPAR_CRS750 = cRefPGNMext.getCSYPAR_PGNM_DIVIorBlank(SYPAR, found_CSYPAR_CRS750, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), "CRS750");
      CRS750DS.set().moveLeft(SYPAR.getPARM());
      if (CRS750DS.getPBACBC() == 1) {
         // Book of account is active
         if (pMaintain.VSER.isBlank()) {
            getFamFunction();
            pMaintain.VSER.set().moveLeftPad(XXVSER);
         }
      }
      // Due date
      if (pMaintain.GPDF.get() && !pMaintain.TEPY.isBlank()) {
         found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, found_CIDMAS, currentCONO, pMaintain.SUNO.get());
         if (found_CIDMAS) {
            found_CSYTAB_TEPY = cRefTEPYext.getCSYTAB_TEPY(SYTAB, found_CSYTAB_TEPY, currentCONO, pMaintain.TEPY.get(), IDMAS.getLNCD());
            if (found_CSYTAB_TEPY) {
               cRefTEPYext.setDSTEPY(SYTAB, DSTEPY);
               // - Fetch due date
               PXRTVDDT.PPCONO = currentCONO;
               PXRTVDDT.PPDIVI.move(pMaintain.DIVI.get());
               PXRTVDDT.PPSTDT = pMaintain.IVDT.get();
               PXRTVDDT.PPSDAY = DSTEPY.getYLSDAY();
               PXRTVDDT.PPTEFM = DSTEPY.getYLTEFM();
               PXRTVDDT.PPTEPD = DSTEPY.getYLTEPD();
               PXRTVDDT.PPPDAY = DSTEPY.getYLPDAY();
               PXRTVDDT.PPNMER = 0;
               PXRTVDDT.PPDUDT = 0;
               PXRTVDDT.PPNERR = 0;
               PXRTVDDT.PPPYNO.move(pMaintain.SUNO.get());
               PXRTVDDT.PPPORT = 'P';
               IN92 = PXRTVDDT.CRTVDDT();
               if (PXRTVDDT.PPNERR != 1) {
                  pMaintain.DUDT.set(PXRTVDDT.PPDUDT);
               }
            }
         }
         pMaintain.setNewEntryContext();
      }
      // Exchange rate type
      if (pMaintain.GPDF.get()) {
         foundParam_CIDVEN.moveLeft(toChar(found_CIDVEN));
         foundParam_CSUDIV.moveLeft(toChar(found_CSUDIV));
         cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, currentCONO, pMaintain.DIVI.get(),  pMaintain.SUNO.get());
         found_CIDVEN = foundParam_CIDVEN.getBoolean();
         found_CSUDIV = foundParam_CSUDIV.getBoolean();
         pMaintain.CRTP.set(IDVEN.getCRTP());
         pMaintain.setNewEntryContext();
      }
      // Path
      if (pMaintain.SINO.get().NE(pMaintain.work_SINO) && !pMaintain.SINO.isBlank()) {
         if (pMaintain.IBTP.get().EQ(cRefIBTPext.SUPPLIER_INVOICE()) ||
             pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE()) || 
             pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) {
            //   - Get hidden image path from CRTIVMG
            //     only used for Automatic data capture, for the moment INVOICE and Prepayment
            PXRTVIMG.clear();
            PXRTVIMG.PPCONO = currentCONO; 	 	
            PXRTVIMG.PPDIVI.moveLeft(pMaintain.DIVI.get()); 	 	
            PXRTVIMG.PPSPYN.moveLeft(pMaintain.SPYN.get()); 	 	
            PXRTVIMG.PPSUNO.moveLeft(pMaintain.SUNO.get()); 	 	
            PXRTVIMG.PPSINO.moveLeft(pMaintain.SINO.get()); 	 	
            //Retrieve path to image using method CRTIVMG()
            PXRTVIMG.CRTVIMG();
            if (pMaintain.PATH.get().NE(PXRTVIMG.PPPATH)) {
               pMaintain.setNewEntryContext();
            }
            pMaintain.PATH.set().moveLeftPad(PXRTVIMG.PPPATH);
         }
      }
      // Cash discount base
      if (equals(pMaintain.TASD.get(), pMaintain.TASD.getDecimals(), 0d) &&
          !equals(pMaintain.CUAM.get(), pMaintain.CUAM.getDecimals(), 0d) &&
          !pMaintain.TECD.get().isBlank()) {
         getFamFunction();
         if (XXCDGN == 2) {
            // Cash discount is based on the net amount
            pMaintain.TASD.set(pMaintain.CUAM.get() - pMaintain.VTAM.get());
         } else {
            pMaintain.TASD.set(pMaintain.CUAM.get());
         }
         initCashDiscount(pMaintain.TASD.get(), 0D);
         pMaintain.setNewEntryContext();
      }
      // Tax included  
      if (pMaintain.GPDF.get())
      {         
         if (MNDIV.getTATM() == 2) {           
            pMaintain.TXIN.set(IDVEN.getTXIN());           
            pMaintain.setNewEntryContext();
         }   
      }
      // Don't set defaults again
      // -------------------------------
      // Defaults for main parameters
      pMaintain.work_getMainDefaults = false;
      // Payee defaults
      if (pMaintain.GPDF.get()) {  
         pMaintain.work_payeeDefaults = true;
         pMaintain.GPDF.set(false);
      }
   }

   /**
   * Sets decimals for amounts in the parameter list based on the currency code.
   */
   public void maintain_setCurrencyDecimals() {
      found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, pMaintain.CUCD.get());
      if (found_CSYTAB_CUCD) {
         cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
         pMaintain.CUAM.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.VTAM.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.TLNA.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.TCHG.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.TOPA.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.TASD.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.CDC1.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.CDC2.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.CDC3.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.TTXA.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.PRPA.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.ADAB.setDecimals(DSCUCD.getYQDCCD());
      }
   }

   /**
   * Rounds amounts in the parameter list to the correct number of decimals.
   */
   public void maintain_roundAmounts() {
      pMaintain.CUAM.set(mvxHalfAdjust(pMaintain.CUAM.get(), pMaintain.CUAM.getDecimals()));
      pMaintain.VTAM.set(mvxHalfAdjust(pMaintain.VTAM.get(), pMaintain.VTAM.getDecimals()));
      pMaintain.TLNA.set(mvxHalfAdjust(pMaintain.TLNA.get(), pMaintain.TLNA.getDecimals()));
      pMaintain.TCHG.set(mvxHalfAdjust(pMaintain.TCHG.get(), pMaintain.TCHG.getDecimals()));
      pMaintain.TOPA.set(mvxHalfAdjust(pMaintain.TOPA.get(), pMaintain.TOPA.getDecimals()));
      pMaintain.TASD.set(mvxHalfAdjust(pMaintain.TASD.get(), pMaintain.TASD.getDecimals()));
      pMaintain.CDC1.set(mvxHalfAdjust(pMaintain.CDC1.get(), pMaintain.CDC1.getDecimals()));
      pMaintain.CDC2.set(mvxHalfAdjust(pMaintain.CDC2.get(), pMaintain.CDC2.getDecimals()));
      pMaintain.CDC3.set(mvxHalfAdjust(pMaintain.CDC3.get(), pMaintain.CDC3.getDecimals()));
      pMaintain.TTXA.set(mvxHalfAdjust(pMaintain.TTXA.get(), pMaintain.TTXA.getDecimals()));
      pMaintain.PRPA.set(mvxHalfAdjust(pMaintain.PRPA.get(), pMaintain.PRPA.getDecimals()));
      pMaintain.ADAB.set(mvxHalfAdjust(pMaintain.ADAB.get(), pMaintain.ADAB.getDecimals()));
   }

   /**
   * Interaction maintain - step UPDATE.
   */
   public void do_maintain_update() {
      executeTransaction(pMaintain.getTransactionName());
      if (pMaintain.messages.existError()) {
         return;
      }
   }

   /**
   * Perform explicit transaction APS450FncINmaintain
   */
   @Transaction(name=cPXAPS450FncINmaintain.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncINmaintain() {
      // Declaration
      boolean found_FAPIBH = false;
      boolean alreadyLocked = false;
      // Check if update is possible
      // =========================================
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().moveLeftPad(pMaintain.DIVI.get());
      if (pMaintain.getMode() == cEnumMode.ADD) {
         // - Init parameters for CRTVNBR (CSYNBR S2,D one counter per company)
         this.PXCONO = currentCONO;
         this.PXDIVI.clear();
         PXRTVNBR.PXNBTY.move("S2");
         PXRTVNBR.PXNBID = 'D';
         PXRTVNBR.PXNERR = '0';
         IN92 = PXRTVNBR.CRTVNBR();
         if (PXRTVNBR.PXNERR == '0') {
         // should not be entered manually when ADD
            pMaintain.INBN.set(PXRTVNBR.PXNBNR);
            APIBH.setINBN(pMaintain.INBN.get());
            pMaintain.INBN.setOUTAccess();
         }
      } else {
         APIBH.setINBN(pMaintain.INBN.get());
         // Lock FAPIBH for update
         if (!lockForUpdate(pMaintain.messages, alreadyLockedByJob, pMaintain.work_CHNO)) {
            return;
         }
         alreadyLocked = alreadyLockedByJob.getBoolean();
      }

      found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      // Update of deleted record
      if (pMaintain.getMode() == cEnumMode.CHANGE && !found_FAPIBH) {
         // MSGID=XDE0001 The record has been deleted by another user
         pMaintain.messages.addError(this.DSPGM, "", "XDE0001");
         return;
      }
      // Add to an existing record
      if (pMaintain.getMode() == cEnumMode.ADD && found_FAPIBH) {
         APIBH.UNLOCK("00");
         // MSGID=XAD0001 A record has been entered by user &1
         pMaintain.messages.addError(this.DSPGM, "", "XAD0001", APIBH.getCHID());
         return;
      }
      // Move parameters to DB-record
      // =========================================
      maintain_update_setValues();
      // Perform update
      // =========================================
      if (maintain_update_perform(found_FAPIBH)) {
         // Exit point for update of extra tables in markets
         exitPointMarket_transaction_APS450FncINmaintain001();
      }
      // Unlock FAPIBH
      if (pMaintain.getMode() == cEnumMode.CHANGE && !alreadyLocked) {
         unlock(pMaintain.messages);
      }
   }

   /**
    * Market modification Exit point 001 for method transaction_APS450FncINmaintain.
    */
   public void exitPointMarket_transaction_APS450FncINmaintain001() {
   }

   /**
   * Sets database field values in step UPDATE of interaction maintain.
   * Moves values from the parameter list to the database fields.
   */
   public void maintain_update_setValues() {
      // Save record
      FAPIBH_record.setRecord(APIBH);
      // Set fields
      APIBH.setIBHE(pMaintain.IBHE.get());
      APIBH.setIBLE(pMaintain.IBLE.get());
      APIBH.setIBTP().move(pMaintain.IBTP.get());
      if (pMaintain.getMode() == cEnumMode.ADD) {
         APIBH.setBIST(pMaintain.BIST.get());
         APIBH.setSUPA(pMaintain.SUPA.get());
      }
      APIBH.setSINO().move(pMaintain.SINO.get());
      APIBH.setSPYN().move(pMaintain.SPYN.get());
      APIBH.setSUNO().move(pMaintain.SUNO.get());
      APIBH.setIVDT(pMaintain.IVDT.get());
      APIBH.setCUCD().move(pMaintain.CUCD.get());
      APIBH.setARAT(pMaintain.ARAT.get());
      APIBH.setTEPY().move(pMaintain.TEPY.get());
      APIBH.setPYME().move(pMaintain.PYME.get());
      APIBH.setBKID().move(pMaintain.BKID.get());
      APIBH.setTDCD().move(pMaintain.TDCD.get());
      APIBH.setCUAM(pMaintain.CUAM.get());
      APIBH.setVTAM(pMaintain.VTAM.get());
      APIBH.setVONO(pMaintain.VONO.get());
      APIBH.setVSER().move(pMaintain.VSER.get());
      APIBH.setACDT(pMaintain.ACDT.get());
      APIBH.setAPCD().move(pMaintain.APCD.get());
      APIBH.setIMCD(pMaintain.IMCD.get().getChar());
      APIBH.setSERS(pMaintain.SERS.get());
      APIBH.setDUDT(pMaintain.DUDT.get());
      APIBH.setFECN().move(pMaintain.FECN.get());
      APIBH.setCRTP(pMaintain.CRTP.get());
      APIBH.setFTCO().move(pMaintain.FTCO.get());
      APIBH.setBSCD().move(pMaintain.BSCD.get());
      APIBH.setTLNA(pMaintain.TLNA.get());
      APIBH.setTCHG(pMaintain.TCHG.get());
      APIBH.setTOPA(pMaintain.TOPA.get());
      APIBH.setPUNO().move(pMaintain.PUNO.get());
      APIBH.setPUDT(pMaintain.PUDT.get());
      APIBH.setTECD().move(pMaintain.TECD.get());
      APIBH.setCDT1(pMaintain.CDT1.get());
      APIBH.setCDT2(pMaintain.CDT2.get());
      APIBH.setCDT3(pMaintain.CDT3.get());
      APIBH.setCDC1(pMaintain.CDC1.get());
      APIBH.setCDC2(pMaintain.CDC2.get());
      APIBH.setCDC3(pMaintain.CDC3.get());
      APIBH.setCDP1(pMaintain.CDP1.get());
      APIBH.setCDP2(pMaintain.CDP2.get());
      APIBH.setCDP3(pMaintain.CDP3.get());
      APIBH.setDNCO().move(pMaintain.DNCO.get());
      APIBH.setSUAC(pMaintain.SUAC.get());
      APIBH.setSBAD(pMaintain.SBAD.get());
      APIBH.setUPBI(pMaintain.UPBI.getInt());
      APIBH.setIAK1().move(pMaintain.IAK1.get());
      APIBH.setIAK2().move(pMaintain.IAK2.get());
      APIBH.setIAK3().move(pMaintain.IAK3.get());
      APIBH.setIAK4().move(pMaintain.IAK4.get());
      APIBH.setIAK5().move(pMaintain.IAK5.get());
      APIBH.setSDAP().move(pMaintain.SDAP.get());
      APIBH.setDNRE().move(pMaintain.DNRE.get());
      APIBH.setPYAD().move(pMaintain.PYAD.get());
      APIBH.setSDA1().move(pMaintain.SDA1.get());
      APIBH.setSDA2().move(pMaintain.SDA2.get());
      APIBH.setSDA3().move(pMaintain.SDA3.get());
      APIBH.setTTXA(pMaintain.TTXA.get());
      APIBH.setTASD(pMaintain.TASD.get());
      APIBH.setPRPA(pMaintain.PRPA.get());
      APIBH.setVRNO().move(pMaintain.VRNO.get());
      APIBH.setTXAP(pMaintain.TXAP.get());
      APIBH.setGEOC(pMaintain.GEOC.get());
      APIBH.setTXIN(pMaintain.TXIN.getInt());
      APIBH.setEALP().move(pMaintain.EALP.get());
      APIBH.setEALR().move(pMaintain.EALR.get());
      APIBH.setEALS().move(pMaintain.EALS.get());
      // Set delivery date depending on parameters in CRS750
      setDeliveryDate(pMaintain.DIVI.get());
      APIBH.setDEDA(pMaintain.DEDA.get());
      APIBH.setADAB(pMaintain.ADAB.get());
      APIBH.setDNOI().move(pMaintain.DNOI.get());
      APIBH.setOYEA(pMaintain.OYEA.get());
      APIBH.setPPYR().move(pMaintain.PPYR.get());
      APIBH.setPPYN().move(pMaintain.PPYN.get());
      APIBH.setYEA4(pMaintain.YEA4.get());
      APIBH.setCORI().move(pMaintain.CORI.get());
   }

   /**
   * Effectuate update in interaction maintain.
   * @param found_FAPIBH
   *    Indicates whether the record was found.
   * @return
   *    True if the update was successful.
   */
   public boolean maintain_update_perform(boolean found_FAPIBH) {
      if (found_FAPIBH) {
         boolean recordChanged = !APIBH.equalsRecord(FAPIBH_record);
         if (recordChanged) {
            FAPIBH_setChanged();
            APIBH.UPDAT("00");
         } else {
            APIBH.UNLOCK("00");
         }
       } else {
         FAPIBH_setChanged();
         APIBH.setJNU().move(this.DSJNU);
         APIBH.setJNA().move(this.DSJNA);
         APIBH.setRGDT(APIBH.getLMDT());
         APIBH.setRGTM(movexTime());
         if (!APIBH.WRITE_CHK("00")) {
            // MSGID=WINBN04 Invoice batch number &1 already exists
            pMaintain.messages.addError(this.DSPGM, "INBN", "WINBN04", CRCommon.formatNumForMsg(APIBH.getINBN()));
            return false;
         }
      }
      // Change data
      pMaintain.RGDT.set(APIBH.getRGDT());
      pMaintain.LMDT.set(APIBH.getLMDT());
      pMaintain.CHID.set().moveLeftPad(APIBH.getCHID());
      return true;
   }

   /**
   * Sets access modes for parameters in interaction maintain for RETRIEVE mode.
   */
   public void maintain_setAccessModeForRetrieve() {
      // Set initial access mode for primary keys and OUT to other parameters
      pMaintain.parameters.setAllOUTAccess();
      // Division
      pMaintain.DIVI.setMANDATORYAccess();
      // Invoice batch number
      pMaintain.INBN.setMANDATORYAccess();
      // Disable parameters depending on invoice type.
      maintain_setDISABLEDAccessMode();
      // Disable access mode for 'get payee defaults'
      pMaintain.GPDF.setDISABLEDAccess(); 
   }

   /**
   * Sets access modes for parameters in interaction maintain for ADD and CHANGE mode.
   */
   public void maintain_setAccessModeForEntry() {
      int currentACYP = 0;
      int dateACDT = 0;
      // Special case in add mode if main parameters have not been set.
      // -------------------------------------------------------
      if (pMaintain.getMode() == cEnumMode.ADD && !pMaintain.work_mainParamsSet) {
         // Set access mode OUT of primary keys, MANDATORY of main parameters and
         // DISABLED of other parameters.
         pMaintain.parameters.setAllDISABLEDAccess();
         pMaintain.DIVI.setMANDATORYAccess();
         if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
            // If not multidivision or if not central user, then the division field should not be displayed
            pMaintain.DIVI.setDISABLEDAccess();
         }
         pMaintain.INBN.setOUTAccess();
         pMaintain.SPYN.setMANDATORYAccess();
         pMaintain.SUNO.setMANDATORYAccess();
         pMaintain.IVDT.setMANDATORYAccess();
         pMaintain.IBTP.setMANDATORYAccess();
         pMaintain.IMCD.setMANDATORYAccess();
         pMaintain.SUPA.setOUTAccess();
         pMaintain.GPDF.setOPTIONALAccess();
         pMaintain.PUNO.setOPTIONALAccess();
         return;
      }
      // -------------------------------------------------------
      // Allow setting of parameter values
      pMaintain.parameters.setAllOPTIONALAccess();
      // Set access mode OUT of primary keys
      // - Division
      pMaintain.DIVI.setOUTAccess();
      // - Invoice batch number
      pMaintain.INBN.setOUTAccess();
      // Set access mode OUT of invoice batch type
      pMaintain.IBTP.setOUTAccess();
      // Set access mode OUT of total line amount
      pMaintain.TLNA.setOUTAccess();
      // - Trade code description
      pMaintain.DSCR.setOUTAccess();
      // Set MANDATORY access
      // - Supplier invoice number
      pMaintain.SINO.setMANDATORYAccess();
      // - Invoice date
      pMaintain.IVDT.setMANDATORYAccess();
      // - Currency rate type
      pMaintain.CRTP.setMANDATORYAccess();
      // - Payee
      pMaintain.SPYN.setMANDATORYAccess();
      // - Supplier
      pMaintain.SUNO.setMANDATORYAccess();
      // - Currency
      pMaintain.CUCD.setMANDATORYAccess();
      // - Invoice matching
      pMaintain.IMCD.setMANDATORYAccess();
      // Payment terms
      pMaintain.TEPY.setMANDATORYAccess();
      // Payment method AP
      pMaintain.PYME.setMANDATORYAccess();
      // Set access mode for some fields if Self billing invoice
      if (pMaintain.IBTP.get().EQ(cRefIBTPext.SELF_BILLING())) {
         // Payee
         pMaintain.SPYN.setOUTAccess();
         // Supplier
         pMaintain.SUNO.setOUTAccess();
         // Currency amount
         pMaintain.CUAM.setOUTAccess();
         // Currency
         pMaintain.CUCD.setOUTAccess();
         // VAT amount
         pMaintain.VTAM.setOUTAccess();
         // Supplier acceptance
         pMaintain.SUAC.setOUTAccess();
         // Conditions for adding lines
         pMaintain.SBAD.setOUTAccess();
         // Invoice per receiving number
         pMaintain.UPBI.setOUTAccess();
         // Our invoicing address
         pMaintain.PYAD.setOUTAccess();
      }
      // Set access mode for some fields if supplier claim invoice
      if (pMaintain.IBTP.get().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
          pMaintain.IBTP.get().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         // Payee
         pMaintain.SPYN.setOUTAccess();
         // Supplier
         pMaintain.SUNO.setOUTAccess();
         // Currency amount
         pMaintain.CUAM.setOUTAccess();
         // Currency
         pMaintain.CUCD.setOUTAccess();
         // VAT amount
         pMaintain.VTAM.setOUTAccess();
         // Supplier acceptance
         pMaintain.SUAC.setOUTAccess();
         // Inoice aggregation key 1
         pMaintain.IAK1.setOUTAccess();
         // Inoice aggregation key 2
         pMaintain.IAK2.setOUTAccess();
         // Inoice aggregation key 3
         pMaintain.IAK3.setOUTAccess();
         // Inoice aggregation key 4
         pMaintain.IAK4.setOUTAccess();
         // Inoice aggregation key 5
         pMaintain.IAK5.setOUTAccess();
         // Our invoicing address
         pMaintain.PYAD.setOUTAccess();
      }
      // Set access mode for some fields if pre-payment
      if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE()) || pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) {
         // Payment reference number
         pMaintain.PPYR.setMANDATORYAccess();
      }
      if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
         // Payment request number
         pMaintain.PPYN.setMANDATORYAccess();
      }
      // Bank account identity
      found_CSYTAB_PYME = cRefPYMEext.getCSYTAB_PYME(SYTAB, found_CSYTAB_PYME, currentCONO, pMaintain.PYME.get());
      cRefPYMEext.setDSPYME(SYTAB, DSPYME);
      found_CSYTAB_PYTP = cRefPYTPext.getCSYTAB_PYTP(SYTAB, found_CSYTAB_PYTP, currentCONO, DSPYME.getFRPYTP());
      cRefPYTPext.setDSPYTP(SYTAB, DSPYTP);
      if (DSPYTP.getFWPYCL() == 4 &&    	
          DSPYME.getFRDOCU().EQ(PYCL4_DOCUFO_FRVCOM) ||
          DSPYTP.getFWPYCL() == 3) {
         pMaintain.BKID.setMANDATORYAccess();
      }
      // Protect AP Standard document if Debit note
      if ((pMaintain.IBTP.get().EQ(cRefIBTPext.DEBIT_NOTE()) || 
          pMaintain.IBTP.get().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) &&
          pMaintain.getMode() != cEnumMode.ADD) {
         // AP Standard document
         pMaintain.SDAP.setOUTAccess();
      }
      // Set access mode for some fields if coming from PPS124 - supplier claim invoice create
      if (LDAZZ.FPNM.EQ("PPS124")) {
         // Payee
         pMaintain.SPYN.setMANDATORYAccess();
         // Supplier
         pMaintain.SUNO.setMANDATORYAccess();
         // Currency
         pMaintain.CUCD.setMANDATORYAccess();
         // Supplier acceptance
         pMaintain.SUAC.setOPTIONALAccess();
         // Inoice aggregation key 1
         pMaintain.IAK1.setOPTIONALAccess();
         // Inoice aggregation key 2
         pMaintain.IAK2.setOPTIONALAccess();
         // Inoice aggregation key 3
         pMaintain.IAK3.setOPTIONALAccess();
         // Inoice aggregation key 4
         pMaintain.IAK4.setOPTIONALAccess();
         // Inoice aggregation key 5
         pMaintain.IAK5.setOPTIONALAccess();
         // Our invoicing address
         pMaintain.PYAD.setOPTIONALAccess();
      }        
      // Set access mode for some fields if coming from PPCRTSBT - create / update self billing invoice
      if (LDAZZ.FPNM.EQ("PPCRTSBT")) {
         // Payee
         pMaintain.SPYN.setMANDATORYAccess();
         // Supplier
         pMaintain.SUNO.setMANDATORYAccess();
         // Currency
         pMaintain.CUCD.setMANDATORYAccess();
         // Supplier acceptance
         pMaintain.SUAC.setOPTIONALAccess();
         // Conditions for adding lines
         pMaintain.SBAD.setOPTIONALAccess();
         // Invoice per receiving number
         pMaintain.UPBI.setOPTIONALAccess();
         // Our invoicing address
         pMaintain.PYAD.setOPTIONALAccess();
      }        
      // Sales tax
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.DIVI.get());
      if (MNDIV.getTATM() == 2) {
         // Invoice matching code
         if (pMaintain.IMCD.get().EQ(cRefIMCDext.NO_PO_MATCHING())) {
            // Geographical code 
            pMaintain.GEOC.setMANDATORYAccess();         
         }
      }
      // Set access mode for Voucher number
      // Call CCHKVON to get voucher number setting method (PLCHKVO.FVMVMA)
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.DIVI.get());
      if (pMaintain.ACDT.get() != 0) {
         dateACDT = pMaintain.ACDT.get();
      } else {
         dateACDT = movexDate();
      }
      if (CRCalendar.lookUpDate(currentCONO, pMaintain.DIVI.get(), dateACDT)) {
         currentACYP = CRCalendar.getFiscalPeriod(MNDIV.getPTFA());
      } else {
         currentACYP = 0;
      }
      PLCHKVO.FVCONO = currentCONO;
      PLCHKVO.FVCMTP = MNCMP.getCMTP();
      PLCHKVO.FVDIVI.move(pMaintain.DIVI.get());
      if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
         PLCHKVO.FVFEID.move("AP53");
      } else if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) {
         PLCHKVO.FVFEID.move("AP54");
      } else {
         PLCHKVO.FVFEID.move("AP50");
      }
      // - Chain CRS750 - Finance parameters
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      found_CSYPAR_CRS750 = cRefPGNMext.getCSYPAR_PGNM_DIVIorBlank(SYPAR, found_CSYPAR_CRS750, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), "CRS750");
      CRS750DS.set().moveLeft(SYPAR.getPARM());
      PLCHKVO.FVACBC = CRS750DS.getPBACBC();
      //if (CRS750DS.getPBACBC() == 1) {
         getFamFunction();
         PLCHKVO.FVVSER.moveLeftPad(XXVSER);
      //}
      PLCHKVO.FVYEA4 = (int)(currentACYP/100);
      if (pMaintain.ACDT.get() == 0) {
         PLCHKVO.FVACDT = movexDate();
      } else {
         PLCHKVO.FVACDT = pMaintain.ACDT.get();
      }
      PLCHKVO.FVVONI = pMaintain.VONO.get();
      PLCHKVO.FVVTST = 1;
      PLCHKVO.FVFETC = 0;
      IN92 = PLCHKVO.CCHKVON();
      if (PLCHKVO.FVMVMA == 0) {
         // manual voucher number setting
         if (pMaintain.partialVld.get()) {
            pMaintain.VONO.setOPTIONALAccess();
         } else {
            pMaintain.VONO.setMANDATORYAccess();
         }
      } else {
         pMaintain.VONO.setOUTAccess();
      }
      // Disable parameters depending on invoice type.
      maintain_setDISABLEDAccessMode();
   }

   /**
   * Sets access mode DISABLED for some parameters depending on invoice type.
   */
   public void maintain_setDISABLEDAccessMode() {
      if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
         // If not multidivision or if not central user, then the division field should not be displayed
         pMaintain.DIVI.setDISABLEDAccess();
      }
      // Disable some parameters if not Debit note
      if (pMaintain.IBTP.get().NE(cRefIBTPext.DEBIT_NOTE()) && 
          pMaintain.IBTP.get().NE(cRefIBTPext.DEBIT_NOTE_REQUEST())) 
      {
         // AP Standard document
         pMaintain.SDAP.setDISABLEDAccess();
         // Debit note reason
         pMaintain.DNRE.setDISABLEDAccess();
         // Text line 1
         pMaintain.SDA1.setDISABLEDAccess();
         // Text line 2
         pMaintain.SDA2.setDISABLEDAccess();
         // Text line 3
         pMaintain.SDA3.setDISABLEDAccess();
      }
      // Disable some parameters if not Self billing invoice
      if (pMaintain.IBTP.get().NE(cRefIBTPext.SELF_BILLING())) {
         // Conditions for adding lines
         pMaintain.SBAD.setDISABLEDAccess();
         // Invoice per receiving number
         pMaintain.UPBI.setDISABLEDAccess();
      }
      // Disable some parameters if not supplier claim invoice
      if (pMaintain.IBTP.get().NE(cRefIBTPext.SUPPLIER_CLAIM()) &&
          pMaintain.IBTP.get().NE(cRefIBTPext.SUPPLIER_CLAIM_REQUEST()))
      {
         // Invoice aggregation key 1
         pMaintain.IAK1.setDISABLEDAccess();
         // Invoice aggregation key 2
         pMaintain.IAK2.setDISABLEDAccess();
         // Invoice aggregation key 3
         pMaintain.IAK3.setDISABLEDAccess();
         // Invoice aggregation key 4
         pMaintain.IAK4.setDISABLEDAccess();
         // Invoice aggregation key 5
         pMaintain.IAK5.setDISABLEDAccess();
      }
      // Disable some parameters if not Self billing invoice, and not supplier claim invoice
      if (pMaintain.IBTP.get().NE(cRefIBTPext.SELF_BILLING()) &&
          pMaintain.IBTP.get().NE(cRefIBTPext.SUPPLIER_CLAIM()) &&
          pMaintain.IBTP.get().NE(cRefIBTPext.SUPPLIER_CLAIM_REQUEST()))
      {
         // Supplier acceptance
         pMaintain.SUAC.setDISABLEDAccess();
         // Our invoicing address
         pMaintain.PYAD.setDISABLEDAccess();
      }
      // Disable some parameters if Supplier claim
      if (pMaintain.IBTP.get().EQ(cRefIBTPext.SUPPLIER_CLAIM()) || pMaintain.IBTP.get().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         // Invoice matching
         pMaintain.IMCD.setDISABLEDAccess();
         pMaintain.IMCD.set().move(cRefIMCDext.NO_PO_MATCHING());
         // Total charges
         pMaintain.TCHG.setDISABLEDAccess();
      }
      // Disable some parameters if not EDI invoice
      if (pMaintain.IBTP.get().GE("00") && pMaintain.IBTP.get().LT("00")) {
         // EDI invoice
      } else {
         // Document code
         pMaintain.DNCO.setDISABLEDAccess();
         // EAN location code supplier
         pMaintain.EALS.setDISABLEDAccess();
         // EAN location code consignee
         pMaintain.EALR.setDISABLEDAccess();
         // EAN location code payee
         pMaintain.EALP.setDISABLEDAccess();
      }
      // Disable some parameters if not pre-payment
      if (pMaintain.IBTP.get().NE(cRefIBTPext.PREPAYMENT_PREINVOICE()) && 
          pMaintain.IBTP.get().NE(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) 
      {
         // Payment reference number
         pMaintain.PPYR.setDISABLEDAccess();
      }
      if (pMaintain.IBTP.get().NE(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
         // Payment request number
         pMaintain.PPYN.setDISABLEDAccess();
         // Payment year
         pMaintain.YEA4.setDISABLEDAccess();
      }
      // - Read APS905 - AP - parameters
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      found_CSYPAR_APS905 = cRefPGNMext.getCSYPAR_PGNM_DIVIorBlank(SYPAR, found_CSYPAR_APS905, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), "APS905");
      APS905DS.set().moveLeft(SYPAR.getPARM());
      if (APS905DS.getYXTDCU() == 0) {
         // Disable Trade code if not used
         pMaintain.TDCD.setDISABLEDAccess();
      }

      // VAT amount
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.DIVI.get());
      if (MNDIV.getTATM() != 1 && MNDIV.getTATM() != 4) {
         pMaintain.VTAM.setDISABLEDAccess();
      }
      // Bank account identity
      cRefPYMEext.getCSYTAB_PYME(SYTAB, found_CSYTAB_PYME, currentCONO, pMaintain.PYME.get());
      cRefPYMEext.setDSPYME(SYTAB, DSPYME);
      cRefPYTPext.getCSYTAB_PYTP(SYTAB, found_CSYTAB_PYTP, currentCONO, DSPYME.getFRPYTP());
      cRefPYTPext.setDSPYTP(SYTAB, DSPYTP);
      if (DSPYTP.getFWPYCL() == 2 || 
         (DSPYTP.getFWPYCL() == 4 &&
          DSPYME.getFRDOCU().NE(PYCL4_DOCUFO_FRVCOM))) {
         pMaintain.BKID.setDISABLEDAccess();
         pMaintain.BKID.clearValue();
      }
      // Get division parameters  
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.DIVI.get());
      // Disable Geographical code and Tax included if not Sales tax
      if (MNDIV.getTATM() != 2) {
         pMaintain.GEOC.setDISABLEDAccess();
         pMaintain.TXIN.setDISABLEDAccess();
      }
      // Sales tax
      if (MNDIV.getTATM() == 2) {
         // Disable Geographical code if Invoice matching
         if (!pMaintain.IMCD.get().EQ(cRefIMCDext.NO_PO_MATCHING())) {
            pMaintain.GEOC.setDISABLEDAccess();
         }   
      }
      // Disable VAT registration no if not VAT
      if (MNDIV.getTATM() != 1 && MNDIV.getTATM() != 4) {
         pMaintain.VRNO.setDISABLEDAccess();
      }
      // Disable From/To country and Base country if not VAT
      if (MNDIV.getTATM() != 1 && MNDIV.getTATM() != 4) {
         pMaintain.FTCO.setDISABLEDAccess();
         pMaintain.BSCD.setDISABLEDAccess();
      }
      // Disable Delivery date if not VAT
      if (MNDIV.getTATM() != 1 && MNDIV.getTATM() != 4) {
         pMaintain.DEDA.setDISABLEDAccess();
      }
      // Disable Voucher number series (internal field)
      pMaintain.VSER.setDISABLEDAccess();
   }

  /**
   * Interaction delete - step INITIATE.
   */
   public void do_delete_initiate() {
      // Get record
      // =========================================
      if (pDelete.passFAPIBH) {
         // Move record from calling program
         moveFAPIBH(APIBH, pDelete.APIBH);
         // Set primary keys
         pDelete.DIVI.set().moveLeftPad(APIBH.getDIVI());
         pDelete.INBN.set(APIBH.getINBN());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pDelete.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         pDelete.INBN.validateMANDATORYandConstraints();
         // Return error messages
         if (pDelete.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pDelete.parameters.setAllDISABLEDAccess();
            pDelete.DIVI.setOUTAccess();
            pDelete.INBN.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBH.setCONO(currentCONO);
         APIBH.setDIVI().move(pDelete.DIVI.get());
         APIBH.setINBN(pDelete.INBN.get());
         if (!APIBH.CHAIN("00", APIBH.getKey("00"))) {
            // MSGID=WINBN03 Invoice batch number &1 does not exist
            pDelete.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pDelete.parameters.setAllDISABLEDAccess();
            pDelete.DIVI.setOUTAccess();
            pDelete.INBN.setOUTAccess();
            return;
         }
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pDelete.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pDelete.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Check status and work in progress
      checkIfWIP(pDelete.messages, alreadyLockedByJob);
      // Return error messages
      if (pDelete.messages.existError()) {
         // Set key parameters to access mode OUT and other parameters to DISABLED.
         pDelete.parameters.setAllDISABLEDAccess();
         pDelete.DIVI.setOUTAccess();
         pDelete.INBN.setOUTAccess();
         return;
      }
      // Set parameters
      // =========================================
      delete_initiate_setParameters();
      // Set Access mode
      // =========================================
      delete_setAccessMode();
      // Add notifications
      // =========================================
      // MSGID=WINBN05 Confirm deletion of invoice batch number &1
      pDelete.messages.addNotification(this.DSPGM, "INBN", "WINBN05", CRCommon.formatNumForMsg(pDelete.INBN.get()));
   }

   /**
   * Sets parameters in step INITIATE of interaction delete.
   */
   public void delete_initiate_setParameters() {
      // Supplier invoice number
      pDelete.SINO.set().moveLeftPad(APIBH.getSINO());
      // Payee
      pDelete.SPYN.set().moveLeftPad(APIBH.getSPYN());
      // Supplier
      pDelete.SUNO.set().moveLeftPad(APIBH.getSUNO());
      // Invoice date
      pDelete.IVDT.set(APIBH.getIVDT());
      // Currency amount
      pDelete.CUAM.set(APIBH.getCUAM());
      // Currency
      pDelete.CUCD.set().moveLeftPad(APIBH.getCUCD());
      if (!pDelete.CUCD.isBlank()) {
         found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, pDelete.CUCD.get());
         if (found_CSYTAB_CUCD) {
            cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
            pDelete.CUAM.setDecimals(DSCUCD.getYQDCCD());
         }
      }
      // Voucher number
      pDelete.VONO.set(APIBH.getVONO());
      // Accounting date
      pDelete.ACDT.set(APIBH.getACDT());
   }

   /**
   * Interaction delete - step VALIDATE.
   */
   public void do_delete_validate() {
      // Nothing to validate
   }

   /**
   * Interaction delete - step UPDATE.
   */
   public void do_delete_update() {
      executeTransaction(pDelete.getTransactionName());
   }

   /**
   * Perform explicit transaction APS450FncINdelete
   */
   @Transaction(name=cPXAPS450FncINdelete.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncINdelete() {
      // Check if delete is possible
      // =========================================
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().moveLeftPad(pDelete.DIVI.get());
      APIBH.setINBN(pDelete.INBN.get());
      // Lock FAPIBH for update
      if (!lockForDelete(pDelete.messages, alreadyLockedByJob)) {
         return;
      }
      // Perform update
      // =========================================
      delete_update_perform();
   }

   /**
   * Effectuate update in interaction delete.
   */
   public void delete_update_perform() {
      // Delete text
      if (APIBH.getTXID() != 0L) {
         this.PXCONO = currentCONO;
         this.PXDIVI.clear();
         this.PXFTXH.moveLeft("FSYTXH00");
         this.PXFTXL.moveLeft("FSYTXL00");
         this.PXTXID = APIBH.getTXID();
         PXCRS98X.CRS984();
      }
      // Delete FAPIBL
      APIBL.setCONO(currentCONO);
      APIBL.setDIVI().moveLeftPad(pDelete.DIVI.get());
      APIBL.setINBN(pDelete.INBN.get());
      APIBL.DELET("00", APIBL.getKey("00", 3));
      // Delete FAPIBA - Supplier invoice batch extra information
      APIBA.setCONO(currentCONO);
      APIBA.setDIVI().moveLeftPad(pDelete.DIVI.get());
      APIBA.setINBN(pDelete.INBN.get());
      APIBA.DELET("00", APIBA.getKey("00", 3));
      // Delete record in FAPIBH
      APIBH.DELET("00", APIBH.getKey("00"));
      if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
          APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         PSUPR.setCONO(APIBH.getCONO());
         PSUPR.setDIVI().move(APIBH.getDIVI());
         PSUPR.setINBN(APIBH.getINBN());
         PSUPR.SETLL("50", PSUPR.getKey("50", 3));
         //   Read records
         while (PSUPR.READE_LOCK("50", PSUPR.getKey("50", 3))) {
            PSUPR.setINBN(0L);
            PSUPR.setCLDT(0);
            PSUPR.setSCTS().move(cRefSCTSext.NEW());
            PSUPR.setLMDT(movexDate());
            PSUPR.setCHID().move(this.DSUSS);
            PSUPR.setCHNO(PSUPR.getCHNO() + 1);
            PSUPR.UPDAT("50");
            // Reset position
            PSUPR.setINBN(APIBH.getINBN());
            PSUPR.SETLL("50", PSUPR.getKey("50", 3));
         }
      }
   }

   /**
   * Sets access mode for interaction delete.
   */
   public void delete_setAccessMode() {
      pDelete.parameters.setAllOUTAccess();
      if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
         // If not multidivision or if not central user, then the division field should not be displayed
         pDelete.DIVI.setDISABLEDAccess();
      }
   }

   /**
   * Interaction copy - step INITIATE.
   */
   public void do_copy_initiate() {
      // Get record
      // =========================================
      if (pCopy.passFAPIBH) {
         // Move record from calling program
         moveFAPIBH(APIBH, pCopy.APIBH);
         // Set primary keys
         pCopy.DIVI.set().moveLeftPad(APIBH.getDIVI());
         pCopy.INBN.set(APIBH.getINBN());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pCopy.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         pCopy.INBN.validateMANDATORYandConstraints();
         // Return error messages
         if (pCopy.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pCopy.parameters.setAllDISABLEDAccess();
            pCopy.DIVI.setOUTAccess();
            pCopy.DIVI.setDISABLEDAccess();
            pCopy.INBN.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBH.setCONO(currentCONO);
         APIBH.setDIVI().move(pCopy.DIVI.get());
         APIBH.setINBN(pCopy.INBN.get());
         if (!APIBH.CHAIN("00", APIBH.getKey("00"))) {
            // MSGID=WINBN03 Invoice batch number &1 does not exist
            pCopy.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pCopy.parameters.setAllDISABLEDAccess();
            if (CRCommon.isCentralUser()) {
               pCopy.DIVI.setOUTAccess();
            } else {
               pCopy.DIVI.setDISABLEDAccess();
            }
            pCopy.INBN.setOUTAccess();
            return;
         }
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pCopy.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pCopy.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Return error messages
      if (pCopy.messages.existError()) {
         // Set key parameters to access mode OUT and other parameters to DISABLED.
         pCopy.parameters.setAllDISABLEDAccess();
         pCopy.DIVI.setOUTAccess();
         pCopy.DIVI.setDISABLEDAccess();
         pCopy.INBN.setOUTAccess();
         return;
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
      // Copy to Division
      pCopy.CPDIVI.set().moveLeftPad(APIBH.getDIVI());
      // Copy to Invoice batch number will be set automatic
      pCopy.CPINBN.clearValue();
   }

   /**
   * Interaction copy - step VALIDATE.
   */
   public void do_copy_validate() {
      //Nothing to validate as a new Invoice batch number will be fetched for the new record
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
   * Perform explicit transaction APS450FncINcopy
   */
   @Transaction(name=cPXAPS450FncINcopy.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncINcopy() {
      // Check if copy is possible
      // =========================================
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().move(pCopy.DIVI.get());
      APIBH.setINBN(pCopy.INBN.get());
      // Copy of deleted record
      if (!APIBH.CHAIN("00", APIBH.getKey("00"))) {
         // MSGID=XDE0001 The record has been deleted by another user
         pCopy.messages.addError(this.DSPGM, "INBN", "XDE0001");
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
      saved_INBN = APIBH.getINBN();
      saved_DIVI.moveLeftPad(APIBH.getDIVI());
      APIBH.setDIVI().move(pCopy.CPDIVI.get());
      // Init parameters for CRTVNBR (CSYNBR S2,D one counter per company)
      this.PXCONO = currentCONO;
      this.PXDIVI.clear();
      PXRTVNBR.PXNBTY.move("S2");
      PXRTVNBR.PXNBID = 'D';
      PXRTVNBR.PXNERR = '0';
      IN92 = PXRTVNBR.CRTVNBR();
      if (PXRTVNBR.PXNERR == '0') {
         // should not be entered manually when ADD
         pCopy.CPINBN.set(PXRTVNBR.PXNBNR);
         APIBH.setINBN(pCopy.CPINBN.get());
      }
      APIBH.setSUPA(cRefSUPAext.NEW());
      APIBH.setIBHE(cRefIBHEext.NO_ERRORS());
      APIBH.setIBLE(cRefIBLEext.NO_ERRORS());
      APIBH.setBIST(cRefBISText.THE_INVOICE_CAN_BE_PROCESSED());
      APIBH.setVONO(0);
      APIBH.setVSER().clear();
      FAPIBH_setChanged();
      APIBH.setJNU().move(this.DSJNU);
      APIBH.setJNA().move(this.DSJNA);
      APIBH.setRGDT(APIBH.getLMDT());
      APIBH.setRGTM(movexTime());
      if (!APIBH.WRITE_CHK("00")) {
         // MSGID=WINBN04 Invoice batch number &1 already exists
          pCopy.messages.addError(this.DSPGM, "CPINBN", "WINBN04", CRCommon.formatNumForMsg(pCopy.CPINBN.get()));
         return false;
      }
      // Copy lines
      APIBL.setCONO(APIBH.getCONO());
      APIBL.setDIVI().moveLeftPad(saved_DIVI);
      APIBL.setINBN(saved_INBN);
      APIBL.SETLL("00", APIBL.getKey("00", 3));
      //   Read records
      while (APIBL.READE("00", APIBL.getKey("00", 3))) {
         APIBL.setDIVI().move(APIBH.getDIVI());
         APIBL.setINBN(APIBH.getINBN());
         APIBL.setRGDT(APIBH.getLMDT());
         APIBL.setRGTM(movexTime());
         if (!APIBL.WRITE_CHK("00")) {
            // MSGID=WTRNO04 Transaction number &1 already exists
             pCopy.messages.addError(this.DSPGM, "CPINBN", "WTRNO04", CRCommon.formatNumForMsg(APIBL.getTRNO()));
            return false;
         }
         APIBL.setDIVI().moveLeftPad(saved_DIVI);
         APIBL.setINBN(saved_INBN);
      }
      // Copy extra information
      APIBA.setCONO(APIBH.getCONO());
      APIBA.setDIVI().moveLeftPad(saved_DIVI);
      APIBA.setINBN(saved_INBN);
      APIBA.SETLL("00", APIBA.getKey("00", 3));
      //   Read records
      while (APIBA.READE("00", APIBA.getKey("00", 3))) {
         APIBA.setDIVI().move(APIBH.getDIVI());
         APIBA.setINBN(APIBH.getINBN());
         APIBA.setRGDT(APIBH.getLMDT());
         APIBA.setRGTM(movexTime());
         if (!APIBA.WRITE_CHK("00")) {
            // MSGID=WPX0104 Extra info number &1 already exists
             pCopy.messages.addError(this.DSPGM, "CPINBN", "WPX0104", CRCommon.formatNumForMsg(APIBA.getPEXN()));
            return false;
         }
         APIBA.setDIVI().moveLeftPad(saved_DIVI);
         APIBA.setINBN(saved_INBN);
      }
      // Copy text
      if (APIBH.getTXID() != 0L) {
         this.PXCONO = currentCONO;
         this.PXDIVI.clear();
         this.PXFTXH.moveLeft("FSYTXH00");
         this.PXFTXL.moveLeft("FSYTXL00");
         this.PXKFLD.moveLeft(APIBH.getINBN(), 10);
         this.PXFILE.moveLeft("FAPIBH00");
         this.PXTXID = APIBH.getTXID();
         this.PXLNCD.clear();
         this.PXTXVR.clear();
         this.PXPICC.clear();
         PXCRS98X.CRS983();
         if (APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
            APIBH.setTXID(this.PXTXID);
            APIBH.UPDAT("00");
         }
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
      // - Division
      pCopy.DIVI.setOUTAccess();
      pCopy.CPDIVI.setOUTAccess();
      if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
         // If not multidivision or if not central user, then the division field should not be displayed
         pCopy.DIVI.setDISABLEDAccess();
         pCopy.CPDIVI.setDISABLEDAccess();
      }
      // - Invoice batch number
      pCopy.INBN.setOUTAccess();
      // Set MANDATORY access
      // - Copy to
      pCopy.CPINBN.setOUTAccess();
   }

   /**
   * Interaction change division - step INITIATE.
   */
   public void do_changeDivision_initiate() {
      // Get record
      // =========================================
      if (pChangeDivision.passFAPIBH) {
         // Move record from calling program
         moveFAPIBH(APIBH, pChangeDivision.APIBH);
         // Set primary keys
         pChangeDivision.DIVI.set().moveLeftPad(APIBH.getDIVI());
         pChangeDivision.INBN.set(APIBH.getINBN());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pChangeDivision.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         pChangeDivision.INBN.validateMANDATORYandConstraints();
         // Return error messages
         if (pChangeDivision.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pChangeDivision.parameters.setAllDISABLEDAccess();
            pChangeDivision.DIVI.setOUTAccess();
            pChangeDivision.INBN.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBH.setCONO(currentCONO);
         APIBH.setDIVI().move(pChangeDivision.DIVI.get());
         APIBH.setINBN(pChangeDivision.INBN.get());
         if (!APIBH.CHAIN("00", APIBH.getKey("00"))) {
            // MSGID=WINBN03 Invoice batch number &1 does not exist
            pChangeDivision.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pChangeDivision.parameters.setAllDISABLEDAccess();
            pChangeDivision.DIVI.setOUTAccess();
            pChangeDivision.INBN.setOUTAccess();
            return;
         }
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pChangeDivision.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pChangeDivision.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      checkIfWIP(pChangeDivision.messages, alreadyLockedByJob);
      if (APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_INVOICE())) {
          // XOP0707 = Change division
         messageData_X_00050_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("XOP0707", "MVXCON")));
         messageData_X_00050_OPT2.moveLeftPad(" 7");
         messageData_X_00050_IBTP.moveLeftPad(APIBH.getIBTP());
         // MSGID=X_00050 &1 (Option &2) cannot be used for invoice type &3
         pChangeDivision.messages.addError(this.DSPGM, "INBN", "X_00050", messageData_X_00050);
      }
      if (!statusOKForChangeDivision(APIBH)) {
         // XOP0707 = Change division
         messageData_X_00051_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("XOP0707", "MVXCON")));
         messageData_X_00051_OPT2.moveLeftPad(" 7");
         messageData_X_00051_SUPA.moveLeftPad(CRCommon.formatNumForMsg(APIBH.getSUPA()));
         // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
         pChangeDivision.messages.addError(this.DSPGM, "INBN", "X_00051", messageData_X_00051);
      }
      // Return error messages
      if (pChangeDivision.messages.existError()) {
         // Set key parameters to access mode OUT
         // and other parameters to DISABLED.
         pChangeDivision.parameters.setAllDISABLEDAccess();
         pChangeDivision.DIVI.setOUTAccess();
         pChangeDivision.INBN.setOUTAccess();
         return;
      }
      // Set parameters
      // =========================================
      changeDivision_initiate_setParameters();
      // Set Access mode
      // =========================================
      changeDivision_setAccessMode();
   }

   /**
   * Sets parameters in step INITIATE of interaction change division.
   */
   public void changeDivision_initiate_setParameters() {
      // Supplier invoice number
      pChangeDivision.SINO.set().moveLeftPad(APIBH.getSINO());
      // Status
      pChangeDivision.SUPA.set(APIBH.getSUPA());
      // Currency amount
      pChangeDivision.CUAM.set(APIBH.getCUAM());
      // Currency
      pChangeDivision.CUCD.set().moveLeftPad(APIBH.getCUCD());
      if (!pChangeDivision.CUCD.isBlank()) {
         found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, pChangeDivision.CUCD.get());
         if (found_CSYTAB_CUCD) {
            cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
            pChangeDivision.CUAM.setDecimals(DSCUCD.getYQDCCD());
            pChangeDivision.ADAB.setDecimals(DSCUCD.getYQDCCD());
         }
      }
      // Supplier
      pChangeDivision.SUNO.set().moveLeftPad(APIBH.getSUNO());
      // Adjusted amount
      pChangeDivision.ADAB.set(APIBH.getADAB());
      // VAT amount
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, APIBH.getDIVI());
      if (MNDIV.getTATM() == 1 ||
          MNDIV.getTATM() == 4) {
         pChangeDivision.VTAM.set(APIBH.getVTAM());
      }
      // Change division
      pChangeDivision.toDIVI.clearValue();
      // Entry date
      pChangeDivision.RGDT.set(APIBH.getRGDT());
      // Change date
      pChangeDivision.LMDT.set(APIBH.getLMDT());
      // Change ID
      pChangeDivision.CHID.set().moveLeftPad(APIBH.getCHID());
      // Change number
      pChangeDivision.work_CHNO = APIBH.getCHNO();
   }

   /**
   * Interaction change division - step VALIDATE.
   */
   public void do_changeDivision_validate() {
      // New division
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      if (MNCMP.getCMTP() != 2) {
         // Change division only allowed for company type 2
         this.MSGDTA.moveLeftPad(" 7");
         pChangeDivision.messages.addError(this.DSPGM, "toDIVI", "XOP0101", this.MSGDTA);
      }
      if (!CRCommon.isCentralUser()) {
         // MSGID=XCM0002 &1 must be maintained from central (blank) division
         pChangeDivision.messages.addError(this.DSPGM, "toDIVI", "XCM0002", SRCOMRCM.getMessage("WDI0110", "MVXCON"));
      } else {
         pChangeDivision.toDIVI.validateMANDATORYandConstraints();
         found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pChangeDivision.toDIVI.get());
         if (pChangeDivision.toDIVI.validateExists(found_CMNDIV)) {
            // Check access authority for division
            PLCHKAD.ADCONO = currentCONO; 	 	
            PLCHKAD.ADCMTP = MNCMP.getCMTP(); 	 	
            PLCHKAD.ADFDIV.move(pChangeDivision.toDIVI.get()); 	 	
            PLCHKAD.ADTDIV.clear();
            PLCHKAD.ADRESP.move(this.DSUSS); 	 	
            PLCHKAD.CCHKACD(); 	 	
            if (PLCHKAD.ADAERR == 1) { 	 	
               pChangeDivision.messages.addError(this.DSPGM, "toDIVI", PLCHKAD.ADMSGI.toString(), PLCHKAD.ADMSGD);
            } 	 	
         }
      }
   }

   /**
   * Interaction change division - step UPDATE.
   */
   public void do_changeDivision_update() {
      executeTransaction(pChangeDivision.getTransactionName());
      if (pChangeDivision.messages.existError()) {
         return;
      }
   }

   /**
   * Perform explicit transaction APS450FncINchangeDivision
   */
   @Transaction(name=cPXAPS450FncINchangeDivision.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncINchangeDivision() {
      // Declaration
      boolean alreadyLocked = false;
      // Check if change division is possible
      // =========================================
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().moveLeftPad(pChangeDivision.DIVI.get());
      APIBH.setINBN(pChangeDivision.INBN.get());
      if (!lockForChangeDivision(pChangeDivision.messages, alreadyLockedByJob, pChangeDivision.work_CHNO)) {
         return;
      }
      alreadyLocked = alreadyLockedByJob.getBoolean();
      // Lock FAPIBH for update
      if (!APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         pChangeDivision.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return;
      }
      // Move parameters to DB-record
      // =========================================
      changeDivision_update_setValues();
      // Perform update
      // =========================================
      changeDivision_update_perform();
      // Unlock FAPIBH
      if (!alreadyLocked) {
         unlock(pChangeDivision.messages);
      }
   }

   /**
   * Sets database field values in step UPDATE of interaction change division.
   * Moves values from the parameter list to the database fields.
   */
   public void changeDivision_update_setValues() {
      APIBH.setSUPA(cRefSUPAext.NEW());
      APIBH.setDIVI().move(pChangeDivision.toDIVI.get());
   }

   /**
   * Effectuate update in interaction change division.
   */
   public void changeDivision_update_perform() {
      FAPIBH_setChanged();
      APIBH.UPDAT("00");
      // Change division in Invoice Lines
      APIBL.setCONO(APIBH.getCONO());
      APIBL.setDIVI().moveLeft(pChangeDivision.DIVI.get());
      APIBL.setINBN(APIBH.getINBN());
      APIBL.SETLL("00", APIBL.getKey("00", 3));
      found_FAPIBL = APIBL.READE_LOCK("00", APIBL.getKey("00", 3));
      while (found_FAPIBL) {
         APIBL.setDIVI().moveLeft(pChangeDivision.toDIVI.get());
         APIBL.setLMDT(movexDate());
         APIBL.setCHID().move(this.DSUSS);
         APIBL.setCHNO(APIBL.getCHNO() + 1);
         APIBL.UPDAT("00");
         // Initiate again for next read
         APIBL.setDIVI().moveLeft(pChangeDivision.DIVI.get());
         APIBL.SETLL("00", APIBL.getKey("00", 3));
         found_FAPIBL = APIBL.READE_LOCK("00", APIBL.getKey("00", 3));
      }
      // Change division in Additional info Invoice
      APIBA.setCONO(APIBH.getCONO());
      APIBA.setDIVI().moveLeft(pChangeDivision.DIVI.get());
      APIBA.setINBN(APIBH.getINBN());
      APIBA.SETLL("00", APIBA.getKey("00", 3));
      found_FAPIBA = APIBA.READE_LOCK("00", APIBA.getKey("00", 3));
      while (found_FAPIBA) {
         APIBA.setDIVI().moveLeft(pChangeDivision.toDIVI.get());
         APIBA.setLMDT(movexDate());
         APIBA.setCHID().move(this.DSUSS);
         APIBA.setCHNO(APIBA.getCHNO() + 1);
         APIBA.UPDAT("00");
         // Initiate again for next read
         APIBA.setDIVI().moveLeft(pChangeDivision.DIVI.get());
         APIBA.SETLL("00", APIBA.getKey("00", 3));
         found_FAPIBA = APIBA.READE_LOCK("00", APIBA.getKey("00", 3));
      }
      // Change data
      pChangeDivision.RGDT.set(APIBH.getRGDT());
      pChangeDivision.LMDT.set(APIBH.getLMDT());
      pChangeDivision.CHID.set().moveLeftPad(APIBH.getCHID());
   }

   /**
   * Sets access mode for interaction change division.
   */
   public void changeDivision_setAccessMode() {
      // Allow setting of parameter values
      pChangeDivision.parameters.setAllOPTIONALAccess();
      // Set key values to OUT
      pChangeDivision.INBN.setOUTAccess();
      pChangeDivision.DIVI.setOUTAccess();
      if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
         // If not multidivision or if not central user, then the division field should not be displayed
         pChangeDivision.DIVI.setDISABLEDAccess();
      }
      pChangeDivision.toDIVI.setMANDATORYAccess();
   }

   /**
   * Interaction reject - step INITIATE.
   */
   public void do_rejectInvoice_initiate() {
      // Get record
      // =========================================
      if (pRejectInvoice.passFAPIBH) {
         // Move record from calling program
         moveFAPIBH(APIBH, pRejectInvoice.APIBH);
         // Set primary keys
         pRejectInvoice.DIVI.set().moveLeftPad(APIBH.getDIVI());
         pRejectInvoice.INBN.set(APIBH.getINBN());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pRejectInvoice.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         pRejectInvoice.INBN.validateMANDATORYandConstraints();
         // Return error messages
         if (pRejectInvoice.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pRejectInvoice.parameters.setAllDISABLEDAccess();
            pRejectInvoice.DIVI.setOUTAccess();
            pRejectInvoice.INBN.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBH.setCONO(currentCONO);
         APIBH.setDIVI().move(pRejectInvoice.DIVI.get());
         APIBH.setINBN(pRejectInvoice.INBN.get());
         if (!APIBH.CHAIN("00", APIBH.getKey("00"))) {
            // MSGID=WINBN03 Invoice batch number &1 does not exist
            pRejectInvoice.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pRejectInvoice.parameters.setAllDISABLEDAccess();
            pRejectInvoice.DIVI.setOUTAccess();
            pRejectInvoice.INBN.setOUTAccess();
            return;
         }
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pRejectInvoice.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pRejectInvoice.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      checkIfWIP(pRejectInvoice.messages, alreadyLockedByJob);
      if (APIBH.getIBTP().NE(cRefIBTPext.SELF_BILLING()) &&
          APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM()) &&
          APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
          // XOP2134 = Reject invoice
         messageData_X_00050_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("XOP2134", "MVXCON")));
         messageData_X_00050_OPT2.moveLeftPad("21");
         messageData_X_00050_IBTP.moveLeftPad(APIBH.getIBTP());
         // MSGID=X_00050 &1 (Option &2) cannot be used for invoice type &3
         pRejectInvoice.messages.addError(this.DSPGM, "INBN", "X_00050", messageData_X_00050);
      }
      if (!statusOKForRejectInvoice(APIBH)) {
         // XOP2134 = Reject invoice
         messageData_X_00051_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("XOP2134", "MVXCON")));
         messageData_X_00051_OPT2.moveLeftPad("21");
         messageData_X_00051_SUPA.moveLeftPad(CRCommon.formatNumForMsg(APIBH.getSUPA()));
         // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
         pRejectInvoice.messages.addError(this.DSPGM, "INBN", "X_00051", messageData_X_00051);
      }
      // Return error messages
      if (pRejectInvoice.messages.existError()) {
         // Set key parameters to access mode OUT
         // and other parameters to DISABLED.
         pRejectInvoice.parameters.setAllDISABLEDAccess();
         pRejectInvoice.DIVI.setOUTAccess();
         pRejectInvoice.INBN.setOUTAccess();
         return;
      }
      // Set parameters
      // =========================================
      rejectInvoice_initiate_setParameters();
      // Set Access mode
      // =========================================
      rejectInvoice_setAccessMode();
   }

   /**
   * Sets parameters in step INITIATE of interaction reject.
   */
   public void rejectInvoice_initiate_setParameters() {
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, APIBH.getDIVI());
      // Supplier invoice number
      pRejectInvoice.SINO.set().moveLeftPad(APIBH.getSINO());
      // Status
      pRejectInvoice.SUPA.set(APIBH.getSUPA());
      // Currency amount
      pRejectInvoice.CUAM.set(APIBH.getCUAM());
      // Currency
      pRejectInvoice.CUCD.set().moveLeftPad(APIBH.getCUCD());
      if (!pRejectInvoice.CUCD.isBlank()) {
         found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, pRejectInvoice.CUCD.get());
         if (found_CSYTAB_CUCD) {
            cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
            pRejectInvoice.CUAM.setDecimals(DSCUCD.getYQDCCD());
            pRejectInvoice.ADAB.setDecimals(DSCUCD.getYQDCCD());
            pRejectInvoice.VTAM.setDecimals(DSCUCD.getYQDCCD());
         }
      }
      // Supplier
      pRejectInvoice.SUNO.set().moveLeftPad(APIBH.getSUNO());
      // Adjusted amount
      pRejectInvoice.ADAB.set(APIBH.getADAB());
      // VAT amount
      if (MNDIV.getTATM() == 1 ||
          MNDIV.getTATM() == 4) {
         pRejectInvoice.VTAM.set(APIBH.getVTAM());
      }
      // Reject reason
      pRejectInvoice.SCRE.set().moveLeftPad(APIBH.getSCRE());
      // Reprint after adjustment
      pRejectInvoice.RPAA.set(APIBH.getRPAA());
      // Reject date
      if (APIBH.getREJD() == 0) {
         //  REJD should be initiated with todays date if blank
         pRejectInvoice.REJD.set(movexDate());
      } else {
         pRejectInvoice.REJD.set(APIBH.getREJD());
      }
      // Text line 1
      pRejectInvoice.SDA1.set().moveLeftPad(APIBH.getSDA1());
      // Text line 2
      pRejectInvoice.SDA2.set().moveLeftPad(APIBH.getSDA2());
      // Text line 3
      pRejectInvoice.SDA3.set().moveLeftPad(APIBH.getSDA3());
      // Entry date
      pRejectInvoice.RGDT.set(APIBH.getRGDT());
      // Change date
      pRejectInvoice.LMDT.set(APIBH.getLMDT());
      // Change ID
      pRejectInvoice.CHID.set().moveLeftPad(APIBH.getCHID());
      // Change number
      pRejectInvoice.work_CHNO = APIBH.getCHNO();
   }

   /**
   * Interaction reject - step VALIDATE.
   */
   public void do_rejectInvoice_validate() {
      //  Reject reason
      pRejectInvoice.SCRE.validateMANDATORYandConstraints();
      found_CSYTAB_SCRE = cRefSCREext.getCSYTAB_SCRE(SYTAB, found_CSYTAB_SCRE, currentCONO, pRejectInvoice.SCRE.get());
      pRejectInvoice.SCRE.validateExists(found_CSYTAB_SCRE);
      //  Reprint after adjustment
      // - No need to validate RPAA since it is a cParamBoolean.
      // Reject date
      pRejectInvoice.REJD.validateMANDATORYandConstraints();
      if (!pRejectInvoice.REJD.isBlank()) {
         if (!CRCalendar.lookUpDate(currentCONO, pRejectInvoice.DIVI.get(), pRejectInvoice.REJD.get())) {
            // MSGID=WRE5301 Rejection date &1 is invalid
            pRejectInvoice.messages.addError(this.DSPGM, "REJD", "WRE5301",
               CRCommon.formatDateForMsg(pRejectInvoice.REJD.get(), pRejectInvoice.getDTFM(), pRejectInvoice.getDSEP()));
         }
      }
      // Text line 1
      pRejectInvoice.SDA1.validateMANDATORYandConstraints();
      // Text line 2
      pRejectInvoice.SDA2.validateMANDATORYandConstraints();
      // Text line 3
      pRejectInvoice.SDA3.validateMANDATORYandConstraints();
   }

   /**
   * Interaction reject - step UPDATE.
   */
   public void do_rejectInvoice_update() {
      executeTransaction(pRejectInvoice.getTransactionName());
      if (pRejectInvoice.messages.existError()) {
         return;
      }
   }

   /**
   * Perform explicit transaction APS450FncINrejectInvoice
   */
   @Transaction(name=cPXAPS450FncINrejectInvoice.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncINrejectInvoice() {
      // Declaration
      boolean alreadyLocked = false;
      // Check if reject is possible
      // =========================================
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().moveLeftPad(pRejectInvoice.DIVI.get());
      APIBH.setINBN(pRejectInvoice.INBN.get());
      if (!lockForRejectInvoice(pRejectInvoice.messages, alreadyLockedByJob, pRejectInvoice.work_CHNO)) {
         return;
      }
      alreadyLocked = alreadyLockedByJob.getBoolean();
      // Lock FAPIBH for update
      if (!APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         pRejectInvoice.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return;
      }
      // Move parameters to DB-record
      // =========================================
      rejectInvoice_update_setValues();
      // Perform update
      // =========================================
      rejectInvoice_update_perform();
      // Unlock FAPIBH
      if (!alreadyLocked) {
         unlock(pRejectInvoice.messages);
      }
   }

   /**
   * Sets database field values in step UPDATE of interaction reject.
   * Moves values from the parameter list to the database fields.
   */
   public void rejectInvoice_update_setValues() {
      APIBH.setSUPA(cRefSUPAext.NOT_APPROVED());
      APIBH.setSCRE().move(pRejectInvoice.SCRE.get());
      APIBH.setRPAA(pRejectInvoice.RPAA.getInt());
      APIBH.setREJD(pRejectInvoice.REJD.get());
      APIBH.setSDA1().move(pRejectInvoice.SDA1.get());
      APIBH.setSDA2().move(pRejectInvoice.SDA2.get());
      APIBH.setSDA3().move(pRejectInvoice.SDA3.get());
   }

   /**
   * Effectuate update in interaction reject.
   */
   public void rejectInvoice_update_perform() {
      FAPIBH_setChanged();
      APIBH.UPDAT("00");
      // Change data
      pRejectInvoice.RGDT.set(APIBH.getRGDT());
      pRejectInvoice.LMDT.set(APIBH.getLMDT());
      pRejectInvoice.CHID.set().moveLeftPad(APIBH.getCHID());
      // Update FAPIBR with reject history
      updateFAPIBR();
   }

   /**
   * Update FAPIBR with Reject history.
   */
   public void updateFAPIBR() {
      APIBR.setCONO(currentCONO);
      APIBR.setDIVI().moveLeftPad(pRejectInvoice.DIVI.get());
      APIBR.setINBN(pRejectInvoice.INBN.get());
      // Fetch next transaction (sequence) number
      APIBR.SETGT("00", APIBR.getKey("00", 3));
      if (!APIBR.REDPE("00", APIBR.getKey("00", 3))) {
         nextTRNO = 1;
      } else {
         nextTRNO = APIBR.getTRNO() + 1;
      }
      APIBR.setTRNO(nextTRNO);
      APIBR.setSCRE().move(pRejectInvoice.SCRE.get());
      APIBR.setRPAA(pRejectInvoice.RPAA.getInt());
      APIBR.setREJD(pRejectInvoice.REJD.get());
      APIBR.setSDA1().move(pRejectInvoice.SDA1.get());
      APIBR.setSDA2().move(pRejectInvoice.SDA2.get());
      APIBR.setSDA3().move(pRejectInvoice.SDA3.get());
      APIBR.setLMDT(movexDate());
      APIBR.setRGDT(APIBR.getLMDT());
      APIBR.setRGTM(movexTime());
      APIBR.setCHID().move(this.DSUSS);
      APIBR.setCHNO(APIBR.getCHNO() + 1);
      if (!APIBR.WRITE_CHK("00")) {
         // Init saved_APIBR to set the length
         if (saved_APIBR == null) {
            saved_APIBR = APIBR.getEmptyRecord();
         }
         // Save record contents from FAPIBR
         saved_APIBR.setRecord(APIBR);
         // Fetch next transaction (sequence) number (If someone else updated at the same time)
         APIBR.SETGT("00", APIBR.getKey("00", 3));
         if (!APIBR.REDPE("00", APIBR.getKey("00", 3))) {
            nextTRNO = 1;
         } else {
            nextTRNO = APIBR.getTRNO() + 1;
         }
         // Restore currenct FAPIBR record information
         APIBR.setRecord(saved_APIBR);
         APIBR.setTRNO(nextTRNO);
         if (!APIBR.WRITE_CHK("00")) {
            // MSGID=WTR3104 Transaction number &1 already exists
            pRejectInvoice.messages.addError(this.DSPGM, "TRNO", "WTR3104", CRCommon.formatNumForMsg(APIBR.getTRNO()));
         }
      }
      // Return transaction number
      pRejectInvoice.TRNO.setOUTAccess();
      if (!pRejectInvoice.messages.existError()) {
         pRejectInvoice.TRNO.set(APIBR.getTRNO());
      }
   }

   /**
   * Sets access mode for interaction reject.
   */
   public void rejectInvoice_setAccessMode() {
      // Allow setting of parameter values
      pRejectInvoice.parameters.setAllOPTIONALAccess();
      // Set key values to OUT
      pRejectInvoice.INBN.setOUTAccess();
      pRejectInvoice.DIVI.setOUTAccess();
      if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
         // If not multidivision or if not central user, then the division field should not be displayed
         pRejectInvoice.DIVI.setDISABLEDAccess();
      }
      // Transaction number
      pRejectInvoice.TRNO.setDISABLEDAccess(); // Will be set to OUT after update.
   }

   /**
   * Interaction Approve - step INITIATE.
   */
   public void do_approveInvoice_initiate() {
      // Get record
      // =========================================
      if (pApproveInvoice.passFAPIBH) {
         // Move record from calling program
         moveFAPIBH(APIBH, pApproveInvoice.APIBH);
         // Set primary keys
         pApproveInvoice.DIVI.set().moveLeftPad(APIBH.getDIVI());
         pApproveInvoice.INBN.set(APIBH.getINBN());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pApproveInvoice.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         pApproveInvoice.INBN.validateMANDATORYandConstraints();
         // Return error messages
         if (pApproveInvoice.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pApproveInvoice.parameters.setAllDISABLEDAccess();
            pApproveInvoice.DIVI.setOUTAccess();
            pApproveInvoice.INBN.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBH.setCONO(currentCONO);
         APIBH.setDIVI().move(pApproveInvoice.DIVI.get());
         APIBH.setINBN(pApproveInvoice.INBN.get());
         if (!APIBH.CHAIN("00", APIBH.getKey("00"))) {
            // MSGID=WINBN03 Invoice batch number &1 does not exist
            pApproveInvoice.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pApproveInvoice.parameters.setAllDISABLEDAccess();
            pApproveInvoice.DIVI.setOUTAccess();
            pApproveInvoice.INBN.setOUTAccess();
            return;
         }
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pApproveInvoice.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pApproveInvoice.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      checkIfWIP(pApproveInvoice.messages, alreadyLockedByJob);
      // Approve Invoice
      if (APIBH.getIBTP().NE(cRefIBTPext.SELF_BILLING()) &&
          APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM()) &&
          APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         // XOP2310 = Approve invoice
         messageData_X_00050_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("XOP2310", "MVXCON")));
         messageData_X_00050_OPT2.moveLeftPad("22");
         messageData_X_00050_IBTP.moveLeftPad(APIBH.getIBTP());
         // MSGID=X_00050 &1 (Option &2) cannot be used for invoice type &3
         pApproveInvoice.messages.addError(this.DSPGM, "INBN", "X_00050", messageData_X_00050);
      }
      // Approve invoice
      if (!statusOKForApproveInvoice(APIBH)) {
         // XOP2310 = Approve invoice
         messageData_X_00051_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("XOP2310", "MVXCON")));
         messageData_X_00051_OPT2.moveLeftPad("22");
         messageData_X_00051_SUPA.moveLeftPad(CRCommon.formatNumForMsg(APIBH.getSUPA()));
         // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
         pApproveInvoice.messages.addError(this.DSPGM, "INBN", "X_00051", messageData_X_00051);
      }
      if (pApproveInvoice.messages.existError()) {
         // Set key parameters to access mode OUT
         // and other parameters to DISABLED.
         pApproveInvoice.parameters.setAllDISABLEDAccess();
         pApproveInvoice.DIVI.setOUTAccess();
         pApproveInvoice.INBN.setOUTAccess();
         return;
      }
      // Set parameters
      // =========================================
      approveInvoice_initiate_setParameters();
      // Set Access mode
      // =========================================
      approveInvoice_setAccessMode();
   }

   /**
   * Sets parameters in step INITIATE of interaction Approve.
   */
   public void approveInvoice_initiate_setParameters() {
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, APIBH.getDIVI());
      // Supplier invoice number
      pApproveInvoice.SINO.set().moveLeftPad(APIBH.getSINO());
      // Status
      pApproveInvoice.SUPA.set(APIBH.getSUPA());
      // Currency amount
      pApproveInvoice.CUAM.set(APIBH.getCUAM());
      // Currency
      pApproveInvoice.CUCD.set().moveLeftPad(APIBH.getCUCD());
      if (!pApproveInvoice.CUCD.isBlank()) {
         found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, pApproveInvoice.CUCD.get());
         if (found_CSYTAB_CUCD) {
            cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
            pApproveInvoice.CUAM.setDecimals(DSCUCD.getYQDCCD());
            pApproveInvoice.ADAB.setDecimals(DSCUCD.getYQDCCD());
            pApproveInvoice.VTAM.setDecimals(DSCUCD.getYQDCCD());
         }
      }
      // Supplier
      pApproveInvoice.SUNO.set().moveLeftPad(APIBH.getSUNO());
      // Adjusted amount
      pApproveInvoice.ADAB.set(APIBH.getADAB());
      // VAT amount
      if (MNDIV.getTATM() == 1 ||
          MNDIV.getTATM() == 4) {
         pApproveInvoice.VTAM.set(APIBH.getVTAM());
      }
      // Approval date
      if (APIBH.getAAPD() == 0) {
         //  AAPD should be initiated with todays date if blank
         pApproveInvoice.AAPD.set(movexDate());
      } else {
         pApproveInvoice.AAPD.set(APIBH.getAAPD());
      }
      // Credit number
      pApproveInvoice.CRNO.set().moveLeftPad(APIBH.getCRNO());
      // Your reference
      pApproveInvoice.YRE1.set().moveLeftPad(APIBH.getYRE1());
      // Invoice date
      pApproveInvoice.IVDT.set(APIBH.getIVDT());
      // Due date
      pApproveInvoice.DUDT.set(APIBH.getDUDT());
      // Entry date
      pApproveInvoice.RGDT.set(APIBH.getRGDT());
      // Change date
      pApproveInvoice.LMDT.set(APIBH.getLMDT());
      // Change ID
      pApproveInvoice.CHID.set().moveLeftPad(APIBH.getCHID());
      // Change number
      pApproveInvoice.work_CHNO = APIBH.getCHNO();
      // ------------------------
      // Save parameter values - to be able to later check if values changes
      pApproveInvoice.work_SINO.moveLeftPad(pApproveInvoice.SINO.get());
   }

   /**
   * Interaction Approve - step VALIDATE.
   */
   public void do_approveInvoice_validate() {
      // Ensure notifications can be issued again (if the basic premises for the notifications has changed)
      if (pApproveInvoice.SINO.get().NE(pApproveInvoice.work_SINO))
      {
         pApproveInvoice.messages.forgetNotification("AP10018");
      }
      // Perform validation
      approveInvoice_validate();
      // Save parameter values - to be able to later check if values changes
      pApproveInvoice.work_SINO.moveLeftPad(pApproveInvoice.SINO.get());
   }

   /**
   * Interaction Approve - step VALIDATE.
   */
   public void approveInvoice_validate() {
      // Supplier invoice number
      if (pApproveInvoice.SINO.isAccessMANDATORYorOPTIONAL()) {
         validateSINO(pApproveInvoice.SINO, pApproveInvoice.DIVI.get(), pApproveInvoice.INBN.get(), pApproveInvoice.SUNO.get(), pApproveInvoice.IVDT.get(), false, pApproveInvoice.messages);
      }
      // Approval date
      pApproveInvoice.AAPD.validateMANDATORYandConstraints();
      if (!pApproveInvoice.AAPD.isBlank()) {
         if (!CRCalendar.lookUpDate(currentCONO, pApproveInvoice.DIVI.get(), pApproveInvoice.AAPD.get())) {
            // MSGID=WAA301 Approval date &1 is invalid
            pApproveInvoice.messages.addError(this.DSPGM, "AAPD", "WAA2301",
               CRCommon.formatDateForMsg(pApproveInvoice.AAPD.get(), pApproveInvoice.getDTFM(), pApproveInvoice.getDSEP()));
         }
      }
      // Credit number
      pApproveInvoice.CRNO.validateMANDATORYandConstraints();
      // Your reference
      pApproveInvoice.YRE1.validateMANDATORYandConstraints();
      // Invoice date
      if (pApproveInvoice.IVDT.isAccessMANDATORYorOPTIONAL()) {
         validateIVDT(pApproveInvoice.IVDT, pApproveInvoice.DIVI.get(), false, pApproveInvoice.messages, pApproveInvoice);
      }
      // Due date
      if (pApproveInvoice.DUDT.isAccessMANDATORYorOPTIONAL()) {
         validateDUDT(pApproveInvoice.DUDT, pApproveInvoice.DIVI.get(), pApproveInvoice.messages, pApproveInvoice);
      }
   }

   /**
   * Interaction Approve - step UPDATE.
   */
   public void do_approveInvoice_update() {
      executeTransaction(pApproveInvoice.getTransactionName());
      if (pApproveInvoice.messages.existError()) {
         return;
      }
   }

   /**
   * Perform explicit transaction APS450FncINapproveInvoice
   */
   @Transaction(name=cPXAPS450FncINapproveInvoice.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncINapproveInvoice() {
      // Declaration
      boolean alreadyLocked = false;
      // Check if update is possible
      // =========================================
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().moveLeftPad(pApproveInvoice.DIVI.get());
      APIBH.setINBN(pApproveInvoice.INBN.get());
      if (!lockForApproveInvoice(pApproveInvoice.messages, alreadyLockedByJob, pApproveInvoice.work_CHNO)) {
         return;
      }
      alreadyLocked = alreadyLockedByJob.getBoolean();
      // Lock FAPIBH for update
      if (!APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         pApproveInvoice.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return;
      }
      // Move parameters to DB-record
      // =========================================
      approveInvoice_update_setValues();
      // Perform update
      // =========================================
      approveInvoice_update_perform();
      // Unlock FAPIBH
      if (!alreadyLocked) {
         unlock(pApproveInvoice.messages);
      }
   }

   /**
   * Sets database field values in step UPDATE of interaction Approve.
   * Moves values from the parameter list to the database fields.
   */
   public void approveInvoice_update_setValues() {
      if (pApproveInvoice.SINO.isAccessMANDATORYorOPTIONAL()) {
         APIBH.setSINO().move(pApproveInvoice.SINO.get());
      }
      APIBH.setSUPA(cRefSUPAext.APPROVED());
      APIBH.setAAPD(pApproveInvoice.AAPD.get());
      APIBH.setCRNO().move(pApproveInvoice.CRNO.get());
      APIBH.setYRE1().move(pApproveInvoice.YRE1.get());
      if (pApproveInvoice.IVDT.isAccessMANDATORYorOPTIONAL()) {
         APIBH.setIVDT(pApproveInvoice.IVDT.get());
      }
      if (pApproveInvoice.DUDT.isAccessMANDATORYorOPTIONAL()) {
         APIBH.setDUDT(pApproveInvoice.DUDT.get());
      }
   }

   /**
   * Effectuate update in interaction adjAppr.
   */
   public void approveInvoice_update_perform() {
      FAPIBH_setChanged();
      APIBH.UPDAT("00");
      // Change data
      pApproveInvoice.RGDT.set(APIBH.getRGDT());
      pApproveInvoice.LMDT.set(APIBH.getLMDT());
      pApproveInvoice.CHID.set().moveLeftPad(APIBH.getCHID());
   }

   /**
   * Sets access mode for interaction adjAppr.
   */
   public void approveInvoice_setAccessMode() {
      // Set access mode OUT to all fields
      pApproveInvoice.parameters.setAllOUTAccess();
      if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
         // If not multidivision or if not central user, then the division field should not be displayed
         pApproveInvoice.DIVI.setDISABLEDAccess();
      }
      // Supplier invoice number
      if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         pApproveInvoice.SINO.setMANDATORYAccess();
      }
      // Approval date
      pApproveInvoice.AAPD.setOPTIONALAccess();
      // Credit number
      pApproveInvoice.CRNO.setOPTIONALAccess();
      // Supplier reference
      pApproveInvoice.YRE1.setOPTIONALAccess();
      // Invoice date
      if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         pApproveInvoice.IVDT.setMANDATORYAccess();
      } else {
         pApproveInvoice.IVDT.setDISABLEDAccess();
      }
      // Due date
      if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         pApproveInvoice.DUDT.setOPTIONALAccess();
      } else {
         pApproveInvoice.DUDT.setDISABLEDAccess();
      }
   }

   /**
   * Interaction Acknowledge - step INITIATE.
   */
   public void do_acknowledge_initiate() {
      // Get record
      // =========================================
      if (pAcknowledge.passFAPIBH) {
         // Move record from calling program
         moveFAPIBH(APIBH, pAcknowledge.APIBH);
         // Set primary keys
         pAcknowledge.DIVI.set().moveLeftPad(APIBH.getDIVI());
         pAcknowledge.INBN.set(APIBH.getINBN());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pAcknowledge.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         pAcknowledge.INBN.validateMANDATORYandConstraints();
         // Return error messages
         if (pAcknowledge.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pAcknowledge.parameters.setAllDISABLEDAccess();
            pAcknowledge.DIVI.setOUTAccess();
            pAcknowledge.INBN.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBH.setCONO(currentCONO);
         APIBH.setDIVI().move(pAcknowledge.DIVI.get());
         APIBH.setINBN(pAcknowledge.INBN.get());
         if (!APIBH.CHAIN("00", APIBH.getKey("00"))) {
            // MSGID=WINBN03 Invoice batch number &1 does not exist
            pAcknowledge.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pAcknowledge.parameters.setAllDISABLEDAccess();
            pAcknowledge.DIVI.setOUTAccess();
            pAcknowledge.INBN.setOUTAccess();
            return;
         }
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pAcknowledge.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pAcknowledge.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      checkIfWIP(pAcknowledge.messages, alreadyLockedByJob);
      // Acknowledge
      if (APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM()) &&
          APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         // X__7800 = Acknowledge
         messageData_X_00050_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("X__7800", "MVXCON")));
         messageData_X_00050_OPT2.moveLeftPad("24");
         messageData_X_00050_IBTP.moveLeftPad(APIBH.getIBTP());
         // MSGID=X_00050 &1 (Option &2) cannot be used for invoice type &3
         pAcknowledge.messages.addError(this.DSPGM, "INBN", "X_00050", messageData_X_00050);
      }
      // Acknowledge
      if (!statusOKForAcknowledge(APIBH)) {
         // X__7800 = Acknowledge
         messageData_X_00051_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("X__7800", "MVXCON")));
         messageData_X_00051_OPT2.moveLeftPad("24");
         messageData_X_00051_SUPA.moveLeftPad(CRCommon.formatNumForMsg(APIBH.getSUPA()));
         // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
         pAcknowledge.messages.addError(this.DSPGM, "INBN", "X_00051", messageData_X_00051);
      }
      if (pAcknowledge.messages.existError()) {
         // Set key parameters to access mode OUT
         // and other parameters to DISABLED.
         pAcknowledge.parameters.setAllDISABLEDAccess();
         pAcknowledge.DIVI.setOUTAccess();
         pAcknowledge.INBN.setOUTAccess();
         return;
      }
      // Set parameters
      // =========================================
      acknowledge_initiate_setParameters();
      // Set Access mode
      // =========================================
      acknowledge_setAccessMode();
   }

   /**
   * Sets parameters in step INITIATE of interaction Acknowledge.
   */
   public void acknowledge_initiate_setParameters() {
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, APIBH.getDIVI());
      // Supplier invoice number
      pAcknowledge.SINO.set().moveLeftPad(APIBH.getSINO());
      // Status
      pAcknowledge.SUPA.set(APIBH.getSUPA());
      // Currency amount
      pAcknowledge.CUAM.set(APIBH.getCUAM());
      // Currency
      pAcknowledge.CUCD.set().moveLeftPad(APIBH.getCUCD());
      if (!pAcknowledge.CUCD.isBlank()) {
         found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, pAcknowledge.CUCD.get());
         if (found_CSYTAB_CUCD) {
            cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
            pAcknowledge.CUAM.setDecimals(DSCUCD.getYQDCCD());
            pAcknowledge.ADAB.setDecimals(DSCUCD.getYQDCCD());
            pAcknowledge.VTAM.setDecimals(DSCUCD.getYQDCCD());
         }
      }
      // Supplier
      pAcknowledge.SUNO.set().moveLeftPad(APIBH.getSUNO());
      // Adjusted amount
      pAcknowledge.ADAB.set(APIBH.getADAB());
      // VAT amount
      if (MNDIV.getTATM() == 1 ||
          MNDIV.getTATM() == 4) {
         pAcknowledge.VTAM.set(APIBH.getVTAM());
      }
      // Credit number
      pAcknowledge.CRNO.set().moveLeftPad(APIBH.getCRNO());
      // Your reference
      pAcknowledge.YRE1.set().moveLeftPad(APIBH.getYRE1());
      // Entry date
      pAcknowledge.RGDT.set(APIBH.getRGDT());
      // Change date
      pAcknowledge.LMDT.set(APIBH.getLMDT());
      // Change ID
      pAcknowledge.CHID.set().moveLeftPad(APIBH.getCHID());
      // Change number
      pAcknowledge.work_CHNO = APIBH.getCHNO();
   }

   /**
   * Interaction Acknowledge - step VALIDATE.
   */
   public void do_acknowledge_validate() {
      // Credit number
      pAcknowledge.CRNO.validateMANDATORYandConstraints();
      // Your reference
      pAcknowledge.YRE1.validateMANDATORYandConstraints();
   }

   /**
   * Interaction Acknowledge - step UPDATE.
   */
   public void do_acknowledge_update() {
      executeTransaction(pAcknowledge.getTransactionName());
      if (pAcknowledge.messages.existError()) {
         return;
      }
   }

   /**
   * Perform explicit transaction APS450FncINacknowledge
   */
   @Transaction(name=cPXAPS450FncINacknowledge.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncINacknowledge() {
      // Declaration
      boolean alreadyLocked = false;
      // Check if update is possible
      // =========================================
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().moveLeftPad(pAcknowledge.DIVI.get());
      APIBH.setINBN(pAcknowledge.INBN.get());
      if (!lockForAcknowledge(pAcknowledge.messages, alreadyLockedByJob, pAcknowledge.work_CHNO)) {
         return;
      }
      alreadyLocked = alreadyLockedByJob.getBoolean();
      // Lock FAPIBH for update
      if (!APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         pAcknowledge.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return;
      }
      // Move parameters to DB-record
      // =========================================
      acknowledge_update_setValues();
      // Perform update
      // =========================================
      acknowledge_update_perform();
      // Unlock FAPIBH
      if (!alreadyLocked) {
         unlock(pAcknowledge.messages);
      }
   }

   /**
   * Sets database field values in step UPDATE of interaction Acknowledge.
   * Moves values from the parameter list to the database fields.
   */
   public void acknowledge_update_setValues() {
      // Note: Acknowledge does not change the status - SUPA.
      APIBH.setCRNO().move(pAcknowledge.CRNO.get());
      APIBH.setYRE1().move(pAcknowledge.YRE1.get());
   }

   /**
   * Effectuate update in interaction adjAppr.
   */
   public void acknowledge_update_perform() {
      FAPIBH_setChanged();
      APIBH.UPDAT("00");
      // Change data
      pAcknowledge.RGDT.set(APIBH.getRGDT());
      pAcknowledge.LMDT.set(APIBH.getLMDT());
      pAcknowledge.CHID.set().moveLeftPad(APIBH.getCHID());
   }

   /**
   * Sets access mode for interaction adjAppr.
   */
   public void acknowledge_setAccessMode() {
      // Set access mode OUT to all fields
      pAcknowledge.parameters.setAllOUTAccess();
      if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
         // If not multidivision or if not central user, then the division field should not be displayed
         pAcknowledge.DIVI.setDISABLEDAccess();
      }
      // Set access mode for Credit number
      pAcknowledge.CRNO.setOPTIONALAccess();
      // Set access mode for Supplier reference
      pAcknowledge.YRE1.setOPTIONALAccess();
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
      SYSTR.setDIVI().clear();
      SYSTR.setPGNM().moveLeftPad("APS450");
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
      // Filter fields
      if (pSettings.FSLP.isBlank()) {
         pSettings.FSLP.set().moveLeftPad(SYSTR.getFSLP());
      } else {
         pSettings.FSLP.validateMANDATORYandConstraints();
         // FSLP is not fully validated since it is validated by the interactive program.
      }
   }

   /**
   * Interaction settings - step VALIDATE.
   */
   public void do_settings_validate() {
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
   * Perform explicit transaction APS450FncINsettings
   */
   @Transaction(name=cPXAPS450FncINsettings.LOGICAL_NAME, primaryTable="CSYSTR") 
   public void transaction_APS450FncINsettings() {
      // Declaration
      boolean found_CSYSTR = false;
      // Check if update is possible
      // =========================================
      SYSTR.setCONO(currentCONO);
      SYSTR.setDIVI().clear();
      SYSTR.setPGNM().moveLeftPad("APS450");
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
      // Filter fields
      SYSTR.setFSLP().moveLeftPad(pSettings.FSLP.get());
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
      // - Filter fields
      pSettings.FSLP.setOUTAccess();
      // Set MANDATORY access
      // - Opening panel
      pSettings.SPIC.setMANDATORYAccess();
      // - Panel sequence
      pSettings.DSEQ.setMANDATORYAccess();
   }

   /**
   * Returns true if in progress status (BIST) can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   *    Returns message WINBN03 if the record does not exist.
   *    Returns message X_00086 if the status of the invoice is not allowed for the operation.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @return
   *    True if in progress status can be set in FAPIBH or already set by same job
   */
   public boolean lockForValidate(cCRMessageList messages, MvxString alreadyLockedByJob) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         // Check invoice status
         if (!statusOKForValidate(APIBH)) {
            // MSGID=X_00086 Selected operation is not allowed for invoice status &1
            messages.addError(this.DSPGM, "INBN", "X_00086", CRCommon.formatNumForMsg(APIBH.getSUPA()));
            APIBH.UNLOCK("00");
            return false;
         }
         // Attempt to soft lock the invoice using BIST
         return lock(cRefBISText.VALIDATION_IN_PROGRESS(), messages, alreadyLockedByJob);
      }
   }

   /**
   * Checks that the status of the invoice is OK for the operation
   * @param APIBH
   *    Reference to the DB-interface of FAPIBH.
   * @return
   *    true if the status is OK.
   */
   public static boolean statusOKForValidate(mvx.db.dta.FAPIBH APIBH) {
      // NOTE: This is declared as a static method since it might be called from other programs as well.
      if (APIBH.getSUPA() < cRefSUPAext.UPDATED_IN_APL()) {
         return true;
      }
      return false;
   }

   /**
   * Operation lockForPrint.
   */
   public void do_lockForPrint() {
      // Validate input parameters
      // =========================================
      // Division
      pLockForPrint.DIVI.validateMANDATORYandConstraints();
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pLockForPrint.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pLockForPrint.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Invoice batch number
      pLockForPrint.INBN.validateMANDATORYandConstraints();
      // Return error messages
      if (pLockForPrint.messages.existError()) {
         return;
      }
      // Perform the operation
      // =========================================
      executeTransaction(pLockForPrint.getTransactionName());
   }

   /**
   * Perform explicit transaction APS450FncOPlockForPrint
   */
   @Transaction(name=cPXAPS450FncOPlockForPrint.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncOPlockForPrint() {
      // Perform the operation
      // =========================================
      // Chain batch invoice
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().move(pLockForPrint.DIVI.get());
      APIBH.setINBN(pLockForPrint.INBN.get());
      // Lock FAPIBH for Print
      lockForPrint(pLockForPrint.messages, alreadyLockedByJob);
      pLockForPrint.alreadyLck.set(alreadyLockedByJob.getBoolean());
   }

   /**
   * Returns true if in progress status (BIST) can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   *    Returns message WINBN03 if the record does not exist.
   *    Returns message X_00086 if the status of the invoice is not allowed for the operation.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @return
   *    True if in progress status can be set in FAPIBH or already set by same job
   */
   public boolean lockForPrint(cCRMessageList messages, MvxString alreadyLockedByJob) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         // Check invoice status
         if (!statusOKForPrint(APIBH)) {
            // MSGID=X_00086 Selected operation is not allowed for invoice status &1
            messages.addError(this.DSPGM, "INBN", "X_00086", CRCommon.formatNumForMsg(APIBH.getSUPA()));
            APIBH.UNLOCK("00");
            return false;
         }
         // Attempt to soft lock the invoice using BIST
         return lock(cRefBISText.PRINTING_IN_PROGRESS(), messages, alreadyLockedByJob);
      }
   }

   /**
   * Checks that the status of the invoice is OK for the operation
   * @param APIBH
   *    Reference to the DB-interface of FAPIBH.
   * @return
   *    true if the status is OK.
   */
   public static boolean statusOKForPrint(mvx.db.dta.FAPIBH APIBH) {
      // NOTE: This is declared as a static method since it might be called from other programs as well.
      if (APIBH.getSUPA() >= cRefSUPAext.VALIDATED()) {
         return true;
      }
      return false;
   }

   /**
   * Operation lockForUpdate.
   */
   public void do_lockForUpdate() {
      // Validate input parameters
      // =========================================
      // Division
      pLockForUpdate.DIVI.validateMANDATORYandConstraints();
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pLockForUpdate.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pLockForUpdate.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Invoice batch number
      pLockForUpdate.INBN.validateMANDATORYandConstraints();
      // Return error messages
      if (pLockForUpdate.messages.existError()) {
         return;
      }
      // Perform the operation
      // =========================================
      executeTransaction(pLockForUpdate.getTransactionName());
   }

   /**
   * Perform explicit transaction APS450FncOPlockForUpdate
   */
   @Transaction(name=cPXAPS450FncOPlockForUpdate.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncOPlockForUpdate() {
      // Perform the operation
      // =========================================
      // Chain batch invoice
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().move(pLockForUpdate.DIVI.get());
      APIBH.setINBN(pLockForUpdate.INBN.get());
      // Lock FAPIBH for update
      APIBH.setCHNO(0);
      lockForUpdate(pLockForUpdate.messages, alreadyLockedByJob, pLockForUpdate.CHNO.get());
      pLockForUpdate.alreadyLck.set(alreadyLockedByJob.getBoolean());
      pLockForUpdate.CHNO.set(APIBH.getCHNO());
   }

   /**
   * Returns true if in progress status (BIST) can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   *    Returns message WINBN03 if the record does not exist.
   *    Returns message XUP0001 if the record has been changed by another user.
   *    Returns message X_00086 if the status of the invoice is not allowed for the operation.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @param currentCHNO
   *    Current change number. Used for checking if the record has been updated by another user.
   *    Send -1 if no such check should be performed.
   * @return
   *    True if in progress status can be set in FAPIBH or already set by same job
   */
   public boolean lockForUpdate(cCRMessageList messages, MvxString alreadyLockedByJob, int currentCHNO) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         // Check if changed by another user
         if (currentCHNO != -1 && currentCHNO != APIBH.getCHNO()) {
            // MSGID=XUP0001 The record has been changed by user &1
            messages.addError(this.DSPGM, "", "XUP0001", APIBH.getCHID());
               APIBH.UNLOCK("00");
               return false;
            }
         // Check invoice status
         if (!statusOKForUpdate(APIBH)) {
            // MSGID=X_00086 Selected operation is not allowed for invoice status &1
            messages.addError(this.DSPGM, "INBN", "X_00086", CRCommon.formatNumForMsg(APIBH.getSUPA()));
            APIBH.UNLOCK("00");
            return false;
         }
         // Attempt to soft lock the invoice using BIST
         return lock(cRefBISText.CHANGE_IN_PROGRESS(), messages, alreadyLockedByJob);
      }
   }

   /**
   * Checks that the status of the invoice is OK for the operation
   * @param APIBH
   *    Reference to the DB-interface of FAPIBH.
   * @return
   *    true if the status is OK.
   */
   public static boolean statusOKForUpdate(mvx.db.dta.FAPIBH APIBH) {
      // NOTE: This is declared as a static method since it might be called from other programs as well.
      if (APIBH.getSUPA() <= cRefSUPAext.VALIDATED_WITH_ERRORS()) {
         return true;
      }
      return false;
   }

   /**
   * Operation lockForUpdOfAPL.
   */
   public void do_lockForUpdateOfAPL() {
      // Validate input parameters
      // =========================================
      // Division
      pLockForUpdAPL.DIVI.validateMANDATORYandConstraints();
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pLockForUpdAPL.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pLockForUpdAPL.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Invoice batch number
      pLockForUpdAPL.INBN.validateMANDATORYandConstraints();
      // Return error messages
      if (pLockForUpdAPL.messages.existError()) {
         return;
      }
      // Perform the operation
      // =========================================
      executeTransaction(pLockForUpdAPL.getTransactionName());
   }

   /**
   * Perform explicit transaction APS450FncOPlockForUpdAPL
   */
   @Transaction(name=cPXAPS450FncOPlockForUpdAPL.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncOPlockForUpdAPL() {
      // Perform the operation
      // =========================================
      // Chain batch invoice
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().move(pLockForUpdAPL.DIVI.get());
      APIBH.setINBN(pLockForUpdAPL.INBN.get());
      // Lock FAPIBH for update
      lockForUpdAPL(pLockForUpdAPL.messages, alreadyLockedByJob);
      pLockForUpdAPL.alreadyLck.set(alreadyLockedByJob.getBoolean());
   }

   /**
   * Returns true if in progress status (BIST) can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   *    Returns message WINBN03 if the record does not exist.
   *    Returns message X_00086 if the status of the invoice is not allowed for the operation.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @return
   *    True if in progress status can be set in FAPIBH or already set by same job
   */
   public boolean lockForUpdAPL(cCRMessageList messages, MvxString alreadyLockedByJob) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         // Check invoice status
         if (!statusOKForUpdAPL(APIBH)) {
            // MSGID=X_00086 Selected operation is not allowed for invoice status &1
            messages.addError(this.DSPGM, "INBN", "X_00086", CRCommon.formatNumForMsg(APIBH.getSUPA()));
            APIBH.UNLOCK("00");
            return false;
         }
         // Attempt to soft lock the invoice using BIST
         return lock(cRefBISText.UPDATE_TO_APL_IN_PROGRESS(), messages, alreadyLockedByJob);
      }
   }

   /**
   * Checks that the status of the invoice is OK for the operation
   * @param APIBH
   *    Reference to the DB-interface of FAPIBH.
   * @return
   *    true if the status is OK.
   */
   public static boolean statusOKForUpdAPL(mvx.db.dta.FAPIBH APIBH) {
      // NOTE: This is declared as a static method since it might be called from other programs as well.
      if (APIBH.getSUPA() == cRefSUPAext.APPROVED() &&
          APIBH.getIBHE() != cRefIBHEext.ERRORS() &&
          APIBH.getIBLE() != cRefIBLEext.ERRORS())
      {
         return true;
      }
      return false;
   }

   /**
   * Operation lockForAdjLine.
   */
   public void do_lockForAdjLine() {
      // Validate input parameters
      // =========================================
      // Division
      pLockForAdjLine.DIVI.validateMANDATORYandConstraints();
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pLockForAdjLine.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pLockForAdjLine.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Invoice batch number
      pLockForAdjLine.INBN.validateMANDATORYandConstraints();
      // Return error messages
      if (pLockForAdjLine.messages.existError()) {
         return;
      }
      // Perform the operation
      // =========================================
      executeTransaction(pLockForAdjLine.getTransactionName());
   }

   /**
   * Perform explicit transaction APS450FncOPlockForAdjLine
   */
   @Transaction(name=cPXAPS450FncOPlockForAdjLine.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncOPlockForAdjLine() {
      // Perform the operation
      // =========================================
      // Chain batch invoice
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().move(pLockForAdjLine.DIVI.get());
      APIBH.setINBN(pLockForAdjLine.INBN.get());
      // Lock FAPIBH for update
      lockForAdjLine(pLockForAdjLine.messages, alreadyLockedByJob);
      pLockForAdjLine.alreadyLck.set(alreadyLockedByJob.getBoolean());
   }

   /**
   * Returns true if in progress status (BIST) can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   *    Returns message WINBN03 if the record does not exist.
   *    Returns message X_00086 if the status of the invoice is not allowed for the operation.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @return
   *    True if in progress status can be set in FAPIBH or already set by same job
   */
   public boolean lockForAdjLine(cCRMessageList messages, MvxString alreadyLockedByJob) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         // Check invoice status
         if (!statusOKForAdjLine(APIBH)) {
            // MSGID=X_00086 Selected operation is not allowed for invoice status &1
            messages.addError(this.DSPGM, "INBN", "X_00086", CRCommon.formatNumForMsg(APIBH.getSUPA()));
            APIBH.UNLOCK("00");
            return false;
         }
         // Attempt to soft lock the invoice using BIST
         return lock(cRefBISText.CHANGE_IN_PROGRESS(), messages, alreadyLockedByJob);
      }
   }

   /**
   * Checks that the status of the invoice is OK for the operation
   * @param APIBH
   *    Reference to the DB-interface of FAPIBH.
   * @return
   *    true if the status is OK.
   */
   public static boolean statusOKForAdjLine(mvx.db.dta.FAPIBH APIBH) {
      // NOTE: This is declared as a static method since it might be called from other programs as well.
      if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING()) ||
          APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) ||
          APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST()))
      {
         return false;
      } else if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         // Valid status for supplier claim request
         if (APIBH.getSUPA() == cRefSUPAext.VALIDATED() ||
             APIBH.getSUPA() == cRefSUPAext.PRINTED() ||
             APIBH.getSUPA() == cRefSUPAext.APPROVED() ||
             APIBH.getSUPA() == cRefSUPAext.NOT_APPROVED() ||
             APIBH.getSUPA() == cRefSUPAext.ADJUSTED_NOT_REPRINTED() ||
             APIBH.getSUPA() == cRefSUPAext.ADJUSTED()) 
         {
            return true;
         }
      } else {
         // Valid status if not supplier claim request
         if (APIBH.getSUPA() == cRefSUPAext.NOT_APPROVED() ||
             APIBH.getSUPA() == cRefSUPAext.ADJUSTED_NOT_REPRINTED() ||
             APIBH.getSUPA() == cRefSUPAext.ADJUSTED()) 
         {
            return true;
         }
      }
      return false;
   }

   /**
   * Returns true if in progress status (BIST) can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   *    Returns message WINBN03 if the record does not exist.
   *    Returns message X_00086 if the status of the invoice is not allowed for the operation.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @return
   *    True if in progress status can be set in FAPIBH or already set by same job
   */
   public boolean lockForDelete(cCRMessageList messages, MvxString alreadyLockedByJob) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         // Check invoice status
         // - No check on status
         // Attempt to soft lock the invoice using BIST
         return lock(cRefBISText.DELETE_IN_PROGRESS(), messages, alreadyLockedByJob);
      }
   }

   /**
   * Returns true if in progress status (BIST) can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   *    Returns message WINBN03 if the record does not exist.
   *    Returns message X_00086 if the status of the invoice is not allowed for the operation.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @param currentCHNO
   *    Current change number. Used for checking if the record has been updated by another user.
   *    Send -1 if no such check should be performed.
   * @return
   *    True if in progress status can be set in FAPIBH or already set by same job
   */
   public boolean lockForChangeDivision(cCRMessageList messages, MvxString alreadyLockedByJob, int currentCHNO) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         // Check if changed by another user
         if (currentCHNO != -1 && currentCHNO != APIBH.getCHNO()) {
            // MSGID=XUP0001 The record has been changed by user &1
            messages.addError(this.DSPGM, "", "XUP0001", APIBH.getCHID());
            APIBH.UNLOCK("00");
            return false;
         }
         // Check invoice status
         if (!statusOKForChangeDivision(APIBH)) {
            // MSGID=X_00086 Selected operation is not allowed for invoice status &1
            messages.addError(this.DSPGM, "INBN", "X_00086", CRCommon.formatNumForMsg(APIBH.getSUPA()));
            APIBH.UNLOCK("00");
            return false;
         }
         // Attempt to soft lock the invoice using BIST
         return lock(cRefBISText.CHANGE_IN_PROGRESS(), messages, alreadyLockedByJob);
      }
   }

   /**
   * Checks that the status of the invoice is OK for the operation
   * @param APIBH
   *    Reference to the DB-interface of FAPIBH.
   * @return
   *    true if the status is OK.
   */
   public static boolean statusOKForChangeDivision(mvx.db.dta.FAPIBH APIBH) {
      // NOTE: This is declared as a static method since it might be called from other programs as well.
      if (APIBH.getSUPA() < cRefSUPAext.UPDATED_IN_APL()) {
         return true;
      }
      return false;
   }

   /**
   * Returns true if in progress status (BIST) can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   *    Returns message WINBN03 if the record does not exist.
   *    Returns message XUP0001 if the record has been changed by another user.
   *    Returns message X_00086 if the status of the invoice is not allowed for the operation.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @param currentCHNO
   *    Current change number. Used for checking if the record has been updated by another user.
   *    Send -1 if no such check should be performed.
   * @return
   *    True if in progress status can be set in FAPIBH or already set by same job
   */
   public boolean lockForRejectInvoice(cCRMessageList messages, MvxString alreadyLockedByJob, int currentCHNO) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         // Check if changed by another user
         if (currentCHNO != -1 && currentCHNO != APIBH.getCHNO()) {
            // MSGID=XUP0001 The record has been changed by user &1
            messages.addError(this.DSPGM, "", "XUP0001", APIBH.getCHID());
            APIBH.UNLOCK("00");
            return false;
         }
         // Check invoice status
         if (!statusOKForRejectInvoice(APIBH)) {
            // MSGID=X_00086 Selected operation is not allowed for invoice status &1
            messages.addError(this.DSPGM, "INBN", "X_00086", CRCommon.formatNumForMsg(APIBH.getSUPA()));
            APIBH.UNLOCK("00");
            return false;
         }
         // Attempt to soft lock the invoice using BIST
         return lock(cRefBISText.CHANGE_IN_PROGRESS(), messages, alreadyLockedByJob);
      }
   }

   /**
   * Checks that the status of the invoice is OK for the operation
   * @param APIBH
   *    Reference to the DB-interface of FAPIBH.
   * @return
   *    true if the status is OK.
   */
   public static boolean statusOKForRejectInvoice(mvx.db.dta.FAPIBH APIBH) {
      // NOTE: This is declared as a static method since it might be called from other programs as well.
      if (APIBH.getSUPA() >= cRefSUPAext.PRINTED() &&
          APIBH.getSUPA() < cRefSUPAext.UPDATED_IN_APL())
      {
         return true;
      }
      return false;
   }

   /**
   * Returns true if in progress status (BIST) can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   *    Returns message WINBN03 if the record does not exist.
   *    Returns message XUP0001 if the record has been changed by another user.
   *    Returns message X_00086 if the status of the invoice is not allowed for the operation.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @param currentCHNO
   *    Current change number. Used for checking if the record has been updated by another user.
   *    Send -1 if no such check should be performed.
   * @return
   *    True if in progress status can be set in FAPIBH or already set by same job
   */
   public boolean lockForApproveInvoice(cCRMessageList messages, MvxString alreadyLockedByJob, int currentCHNO) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         // Check if changed by another user
         if (currentCHNO != -1 && currentCHNO != APIBH.getCHNO()) {
            // MSGID=XUP0001 The record has been changed by user &1
            messages.addError(this.DSPGM, "", "XUP0001", APIBH.getCHID());
            APIBH.UNLOCK("00");
            return false;
         }
         // Check invoice status
         if (!statusOKForApproveInvoice(APIBH)) {
            // MSGID=X_00086 Selected operation is not allowed for invoice status &1
            messages.addError(this.DSPGM, "INBN", "X_00086", CRCommon.formatNumForMsg(APIBH.getSUPA()));
            APIBH.UNLOCK("00");
            return false;
         }
         // Attempt to soft lock the invoice using BIST
         return lock(cRefBISText.CHANGE_IN_PROGRESS(), messages, alreadyLockedByJob);
      }
   }

   /**
   * Checks that the status of the invoice is OK for the operation
   * @param APIBH
   *    Reference to the DB-interface of FAPIBH.
   * @return
   *    true if the status is OK.
   */
   public static boolean statusOKForApproveInvoice(mvx.db.dta.FAPIBH APIBH) {
      // NOTE: This is declared as a static method since it might be called from other programs as well.
      if (APIBH.getSUPA() == cRefSUPAext.PRINTED() ||
          APIBH.getSUPA() == cRefSUPAext.ADJUSTED_AND_REPRINTED() ||
          APIBH.getSUPA() == cRefSUPAext.ADJUSTED())
      {
         return true;
      }
      return false;
   }

   /**
   * Returns true if in progress status (BIST) can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   *    Returns message WINBN03 if the record does not exist.
   *    Returns message XUP0001 if the record has been changed by another user.
   *    Returns message X_00086 if the status of the invoice is not allowed for the operation.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @param currentCHNO
   *    Current change number. Used for checking if the record has been updated by another user.
   *    Send -1 if no such check should be performed.
   * @return
   *    True if in progress status can be set in FAPIBH or already set by same job
   */
   public boolean lockForAcknowledge(cCRMessageList messages, MvxString alreadyLockedByJob, int currentCHNO) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         // Check if changed by another user
         if (currentCHNO != -1 && currentCHNO != APIBH.getCHNO()) {
            // MSGID=XUP0001 The record has been changed by user &1
            messages.addError(this.DSPGM, "", "XUP0001", APIBH.getCHID());
            APIBH.UNLOCK("00");
            return false;
         }
         // Check invoice status
         if (!statusOKForAcknowledge(APIBH)) {
            // MSGID=X_00086 Selected operation is not allowed for invoice status &1
            messages.addError(this.DSPGM, "INBN", "X_00086", CRCommon.formatNumForMsg(APIBH.getSUPA()));
            APIBH.UNLOCK("00");
            return false;
         }
         // Attempt to soft lock the invoice using BIST
         return lock(cRefBISText.CHANGE_IN_PROGRESS(), messages, alreadyLockedByJob);
      }
   }

   /**
   * Checks that the status of the invoice is OK for the operation
   * @param APIBH
   *    Reference to the DB-interface of FAPIBH.
   * @return
   *    true if the status is OK.
   */
   public static boolean statusOKForAcknowledge(mvx.db.dta.FAPIBH APIBH) {
      // NOTE: This is declared as a static method since it might be called from other programs as well.
      if (APIBH.getSUPA() == cRefSUPAext.PRINTED())
      {
         return true;
      }
      return false;
   }

   /**
   * Returns true if in progress status (BIST) can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   *    Returns message WINBN03 if the record does not exist.
   *    Returns message X_00086 if the status of the invoice is not allowed for the operation.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @return
   *    True if in progress status can be set in FAPIBH or already set by same job
   */
   public boolean lockForReversePrintout(cCRMessageList messages, MvxString alreadyLockedByJob) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         // Check invoice status
         if (!statusOKForReversePrintout(APIBH)) {
            // MSGID=X_00086 Selected operation is not allowed for invoice status &1
            messages.addError(this.DSPGM, "INBN", "X_00086", CRCommon.formatNumForMsg(APIBH.getSUPA()));
            APIBH.UNLOCK("00");
            return false;
         }
         // Attempt to soft lock the invoice using BIST
         return lock(cRefBISText.CHANGE_IN_PROGRESS(), messages, alreadyLockedByJob);
      }
   }

   /**
   * Checks that the status of the invoice is OK for the operation
   * @param APIBH
   *    Reference to the DB-interface of FAPIBH.
   * @return
   *    true if the status is OK.
   */
   public static boolean statusOKForReversePrintout(mvx.db.dta.FAPIBH APIBH) {
      // NOTE: This is declared as a static method since it might be called from other programs as well.
      if (APIBH.getSUPA() == cRefSUPAext.PRINTED()) {
         return true;
      }
      return false;
   }

   /** 
   * Attempts to lock the invoice (set BIST in FAPIBH).
   * Called by methods lockFor...
   * @param BIST
   *    Desired 'work in progress' status
   * @param messages
   *    Container with list of returned messages
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @return
   *    True if BIST was set in FAPIBH
   */   
   public boolean lock(int BIST, cCRMessageList messages, MvxString alreadyLockedByJob) {
      alreadyLockedByJob.moveLeft(toChar(false));
      if (APIBH.getBIST() != cRefBISText.THE_INVOICE_CAN_BE_PROCESSED() &&
          APIBH.getJNU().EQ(this.DSJNU) &&
          APIBH.getJNA().EQ(this.DSJNA) &&
          APIBH.getCHID().EQ(this.DSUSS)) {
         APIBH.UNLOCK("00");
         alreadyLockedByJob.moveLeft(toChar(true));
         return true;
      } else if (APIBH.getBIST() == cRefBISText.THE_INVOICE_CAN_BE_PROCESSED()) {
         APIBH.setBIST(BIST);
         APIBH.setJNU().move(this.DSJNU);
         APIBH.setJNA().move(this.DSJNA);
         APIBH.setLMDT(movexDate());
         APIBH.setCHID().move(this.DSUSS);
         APIBH.UPDAT("00");
         return true;
      } else {
         // The invoice is work in progress status. Is it stuck?
         if (checkIfStuckWIP(APIBH.getJNA(), APIBH.getCHID(), APIBH.getJNU())) {
            // MSGID=AP_0029 Invoice batch number &1 is STUCK in the status of work in progress. User &2.
            messageData_AP_0029_INBN.moveLeft(APIBH.getINBN(), 15);
            messageData_AP_0029_CHID.moveLeftPad(APIBH.getCHID());
            messages.addError(this.DSPGM, "INBN", "AP_0029", messageData_AP_0029);

         } else {
            // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
            messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
            messageData_AP_0028_CHID.moveLeftPad(APIBH.getCHID());
            messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         }
         APIBH.UNLOCK("00");
         return false;
      }
   }

   /**
   * Operation unlock.
   */
   public void do_unlock() {
      // Validate input parameters
      // =========================================
      // Division
      pUnlock.DIVI.validateMANDATORYandConstraints();
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pUnlock.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pUnlock.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Invoice batch number
      pUnlock.INBN.validateMANDATORYandConstraints();
      // Return error messages
      if (pUnlock.messages.existError()) {
         return;
      }
      // Perform the operation
      // =========================================
      executeTransaction(pUnlock.getTransactionName());
   }

   /**
   * Perform explicit transaction APS450FncOPunlock
   */
   @Transaction(name=cPXAPS450FncOPunlock.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncOPunlock() {
      // Perform the operation
      // =========================================
      // Chain batch invoice
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().move(pUnlock.DIVI.get());
      APIBH.setINBN(pUnlock.INBN.get());
      // Lock FAPIBH for update
      unlock(pUnlock.messages);
   }

   /**
   * Returns true if Status Update in progress can be set in FAPIBH
   * @param messages
   *    Container with list of messages
   * @return
   *    true if Status Update in progress can be set in FAPIBH
   */
   public boolean unlock(cCRMessageList messages) {
      if (!APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else {
         if (APIBH.getBIST() != cRefBISText.THE_INVOICE_CAN_BE_PROCESSED()) {
            if (APIBH.getJNU().NE(this.DSJNU) ||
                APIBH.getJNA().NE(this.DSJNA)) {
               // MSGID=X_00061 Trying to unlock a lock set by another job.
               messages.addError(this.DSPGM, "INBN", "X_00061");
               APIBH.UNLOCK("00");
               return false;
            } else {
               APIBH.setBIST(cRefBISText.THE_INVOICE_CAN_BE_PROCESSED());
               APIBH.setJNU().move(this.DSJNU);
               APIBH.setJNA().move(this.DSJNA);
               APIBH.UPDAT("00");
               return true;
            }
         } else {
            // MSGID=X_00046 Invoice progress is &1 and Invoice status is &2. Update is not allowed.
            messageData_X_00046_BIST.moveLeft((long)APIBH.getBIST(), 15);
            messageData_X_00046_SUPA.moveLeft((long)APIBH.getSUPA(), 15);
            messages.addError(this.DSPGM, "INBN", "X_00046", messageData_X_00046);
            APIBH.UNLOCK("00");
            return false;
         }
      }
   }

  /**
   * Operation checkIfWIP.
   */
   public void do_checkIfWIP() {
      // Validate input parameters
      // =========================================
      // Division
      pCheckIfWIP.DIVI.validateMANDATORYandConstraints();
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pCheckIfWIP.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pCheckIfWIP.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Invoice batch number
      pCheckIfWIP.INBN.validateMANDATORYandConstraints();
      // Return error messages
      if (pCheckIfWIP.messages.existError()) {
         return;
      }
      // Perform the operation
      // =========================================
      // Check status of batch invoice head
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().move(pCheckIfWIP.DIVI.get());
      APIBH.setINBN(pCheckIfWIP.INBN.get());
      if (!APIBH.CHAIN("00", APIBH.getKey("00"))) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         pCheckIfWIP.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return;
      } else {
         checkIfWIP(pCheckIfWIP.messages, alreadyLockedByJob);
         pCheckIfWIP.alreadyLck.set(alreadyLockedByJob.getBoolean());
      }
   }

   /**
   * Returns true if supplier invoice batch is work in progress (FAPIBH).
   * Doesn't return true if the current job is locking the invoice.
   * In that case flag alreadyLockedByJob is turned on.
   * FAPIBH should already have been read before calling this method.
   * NOTE: THIS METHOD SHOULD BE CALLED BEFORE ANY FIELD ON THE RECORD IS
   * ACCESSED, SINCE IT MIGHT RE-READ THE RECORD.
   * @param messages
   *    Error message is returned if ivoice is work in progress.
   *    Returns message AP_0028 if the record is already soft locked.
   *    Returns message AP_0029 if the record is stuck in soft locked, i.e. the locking job is dead.
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @return
   *    true if supplier invoice batch is Work in progress (FAPIBH)
   */
   public boolean checkIfWIP(cCRMessageList messages, MvxString alreadyLockedByJob) {
      alreadyLockedByJob.moveLeft(toChar(false));
      if (APIBH.getBIST() == cRefBISText.THE_INVOICE_CAN_BE_PROCESSED()) {
         // BIST could be locked in an uncommitted explicit transaction
         if (checkIfWIPinExplicitTransaction()) {
            // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
            messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
            messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
            messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
            return true;
         }
      }
      if (APIBH.getBIST() != cRefBISText.THE_INVOICE_CAN_BE_PROCESSED()) {
         if (APIBH.getJNU().EQ(this.DSJNU) &&
             APIBH.getJNA().EQ(this.DSJNA) &&
             APIBH.getCHID().EQ(this.DSUSS)) 
         {
            // The WIP status is already set by this job
            alreadyLockedByJob.moveLeft(toChar(true));
            return false;
         } else {
            // The invoice is work in progress status. Is it stuck?
            if (checkIfStuckWIP(APIBH.getJNA(), APIBH.getCHID(), APIBH.getJNU())) {
               // MSGID=AP_0029 Invoice batch number &1 is STUCK in the status of work in progress. User &2.
               messageData_AP_0029_INBN.moveLeft(APIBH.getINBN(), 15);
               messageData_AP_0029_CHID.moveLeftPad(APIBH.getCHID());
               messages.addError(this.DSPGM, "INBN", "AP_0029", messageData_AP_0029);
            } else {
               // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
               messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
               messageData_AP_0028_CHID.moveLeftPad(APIBH.getCHID());
               messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
            }
            return true;
         }
      }
      return false;
   }

   /**
   * Checks if supplier invoice (FAPIBH) is currently locked for update 
   * in an explicit transaction.
   * FAPIBH should already have been read before calling this method.
   * The check is only performed if FAPIBH.BIST = 0 (THE_INVOICE_CAN_BE_PROCESSED).
   * If found to be locked in an explicit transaction, then 
   * FAPIBH.BIST is set to 5 (UPDATE_IN_PROGRESS).
   * NOTE: THIS METHOD SHOULD BE CALLED BEFORE ANY FIELD ON THE RECORD IS
   * ACCESSED, SINCE IT MIGHT RE-READ THE RECORD.
   * @return
   *    true if the supplier invoice is locked for update in an explicit transaction
   */
   public boolean checkIfWIPinExplicitTransaction() {
      if (!activeTrans) {
         // No need to check if explict transactions are turned off
         return false;
      }
      if (APIBH.getBIST() != cRefBISText.THE_INVOICE_CAN_BE_PROCESSED()) {
         // No need to check if we already know that the record is WIP
         return false;
      }
      // Check if locked for update in other explicit transaction
      switch (APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"))) {
         case -1:
            // Record already locked in other explicit transaction.
            // Indicate this on the WIP-flag.
            APIBH.setBIST(cRefBISText.UPDATE_IN_PROGRESS());
            return true;
         case 1:
            // Lock of record successful --> unlock it.
            // (No other explicit transaction is locking it!)
            APIBH.UNLOCK("00");
            return false;
      }
      // Record not found
      return false;
   }

   /**
   * Call when invoice is set as work in progress (BIST!=0) to check if the invoice is
   * stuck in work in progress.
   * @return
   *    Returns true if the invoice is stuck in work in progress.
   *    This is true when the job which set WIP is no longer active in
   *    the system, or if it is the current job that is locking the record.
   */
   public boolean checkIfStuckWIP(MvxString JNA, MvxString CHID, MvxString JNU) {
      // Check if job is active
      if (SRCOMCJB.CHKJOB(JNA, CHID, JNU.getInt()) == 1) {
         // Job is still active.
         if (JNA.EQ(DSJNA) && CHID.EQ(DSUSS) && JNU.EQ(DSJNU)) {
            // Current job set WIP -> stuck in WIP
            return true;
         } else {
            // Other job set WIP -> not possible to determine if stuck.
            return false;
         }
      } else {
         // Job is no longer active -> stuck in WIP
         return true;
      }
   }

   /**
   * Operation validateInvoice.
   */
   public void do_validateInvoice() {
      // Clear old errors in the error log
      if (getFAPIBH(pValidateInv.DIVI.get(), pValidateInv.INBN.get())) {
         clearErrorLog();
      }
      // Division
      pValidateInv.DIVI.validateMANDATORYandConstraints();
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pValidateInv.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pValidateInv.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Invoice batch number
      pValidateInv.INBN.validateMANDATORYandConstraints();
      // Return error messages
      if (pValidateInv.messages.existError()) {
         // Add errors to error log
         addToErrorLog_head(pValidateInv.messages);
         return;
      }
      // Perform the operation
      // =========================================
      executeTransaction(pValidateInv.getTransactionName());
   }

   /**
   * Perform explicit transaction APS450FncOPvalidateInv
   */
   @Transaction(name=cPXAPS450FncOPvalidateInv.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncOPvalidateInv() {
      // Declaration
      boolean alreadyLocked = false;
      // Validate head
      pAPS450Fnc_maintain = get_pAPS450Fnc_maintain();
      pMaintain = pAPS450Fnc_maintain; // prepare local operation call
      // - lock invoice for validate
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().move(pValidateInv.DIVI.get());
      APIBH.setINBN(pValidateInv.INBN.get());
      // Set Invoice batch number to status - Validation in progress
      if (!lockForValidate(pValidateInv.messages, alreadyLockedByJob)) {
         pMaintain = null; // end local operation call
         // Add errors to error log
         addToErrorLog_head(pValidateInv.messages);
         return;
      }
      alreadyLocked = alreadyLockedByJob.getBoolean();
      if (!validateHeader()) {
         pMaintain = null; // end local operation call
         // Unlock FAPIBH
         if (!alreadyLocked) {
            unlock(pValidateInv.messages);
         }
         return;
      }    
      pMaintain = null; // end local operation call
      // Create rounding transaction if needed
      createRoundingTransaction();
      // Validate lines
      validateLines();
      // Accumulate FAPIBL.IBLE to FAPIBH.IBLE
      accumulateLineErr(pValidateInv.messages);
      // Set status - SUPA
      if (APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
         if (APIBH.getIBHE() == cRefIBHEext.ERRORS() || APIBH.getIBLE() == cRefIBLEext.ERRORS()) {
            // Set status errors found
            APIBH.setSUPA(cRefSUPAext.VALIDATED_WITH_ERRORS());
         } else {
            if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_INVOICE()) ||
                APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) ||
                APIBH.getIBTP().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE()) || 
                APIBH.getIBTP().EQ(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) {
               // Set status approved
               APIBH.setSUPA(cRefSUPAext.APPROVED());
            } else if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
               // Set status validated
               APIBH.setSUPA(cRefSUPAext.VALIDATED());
            } else if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING())) {
               int SUAClimitDate = 0;
               // Next status depends on supplier acceptance settings - SUAC
               if (APIBH.getSUAC() == 3) {
                  // Check self billing agreement
                  int AUT3 = 0;
                  APIBL.setCONO(APIBH.getCONO());
                  APIBL.setDIVI().move(APIBH.getDIVI());
                  APIBL.setINBN(APIBH.getINBN());
                  APIBL.setRDTP(cRefRDTPext.ITEM_LINE());
                  APIBL.SETLL("10", APIBL.getKey("10", 4));
                  if (APIBL.READE("10", APIBL.getKey("10", 4))) {
                     found_MPAGRS = cRefSBANext.getMPAGRS(PAGRS, found_MPAGRS, APIBH.getCONO(), APIBH.getSUNO(), APIBL.getSBAN());
                     AUT3 = PAGRS.getAUT3();
                  }
                  // Calculate SUAC limit date
                  if (CRCalendar.lookUpDate(APIBH.getCONO(), APIBH.getDIVI(), APIBH.getIVDT())) {
                     if (CRCalendar.convertCenturyDayNo(APIBH.getCONO(), APIBH.getDIVI(), 
                            CRCalendar.getCenturyDayNo() + AUT3)) 
                     {
                        SUAClimitDate = CRCalendar.getDate();
                     }
                  }
               }
               if (APIBH.getSUAC() == 0 ||
                   APIBH.getSUAC() == 3 && movexDate() >= SUAClimitDate)
               {
                  APIBH.setSUPA(cRefSUPAext.APPROVED());
               } else {
                  // SUAC 1, 2 or SUAC 3 but within time limit --> held for printing
                  APIBH.setSUPA(cRefSUPAext.VALIDATED());
               }
            } else {
               // Set status validated
               APIBH.setSUPA(cRefSUPAext.VALIDATED());
            }
         }
         FAPIBH_setChanged();
         APIBH.UPDAT("00");
      }
      // Unlock FAPIBH
      if (!alreadyLocked) {
         unlock(pValidateInv.messages);
      }
   }

   /**
   * Returns true if header record (FAPIBH) could be validate.
   * @return 
   *    true if header record (FAPIBH) could be validate.
   */
   public boolean validateHeader() {
      boolean errorOnInitiate;
      boolean errorOnValidate;
      boolean newEntryContext;
      // - call maintain_initiate, maintain_validate in APS450Fnc
      // Call APS450Fnc, maintain - step initiate
      // =========================================
      pMaintain.messages.forgetNotifications();
      pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
      pMaintain.indicateAutomated();
      // Set primary keys
      // - Division
      pMaintain.DIVI.set().moveLeftPad(APIBH.getDIVI());
      // - Invoice batch number
      pMaintain.INBN.set(APIBH.getINBN());
      // Pass a reference to the record.
      pMaintain.APIBH = APIBH;
      pMaintain.passFAPIBH = true;
      passExtensionTable(); // exit point for modification
      // =========================================
      pMaintain.enterFnc(this); // local operation call start
      do_maintain_initiate();
      pMaintain.exitFnc(); // local operation call end
      // =========================================
      // Handle messages
      errorOnInitiate = validateInvoice_handleMessages(pMaintain.messages);
      if (errorOnInitiate) {
         // Add errors to error log
         addToErrorLog_head(pMaintain.messages);
         // Release resources allocated by the parameter list.
         pMaintain.release();
         return false;
      }
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Call APS450Fnc, maintain - step validate
      // =========================================
      do {
         pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.VALIDATE);
         // =========================================
         pMaintain.enterFnc(this); // local operation call start
         do_maintain_validate();
         pMaintain.exitFnc(); // local operation call end
         // =========================================
         // Handle messages
         errorOnValidate = validateInvoice_handleMessages(pMaintain.messages);
         newEntryContext = pMaintain.isNewEntryContext();
         if (errorOnValidate) {
            // Add errors to error log
            addToErrorLog_head(pMaintain.messages);
         }
         // Release resources allocated by the parameter list.
         pMaintain.release();
      } while (newEntryContext);
      return true;
   }

   /**
   * Validates Supplier invoice batch lines.
   */
   public void validateLines() {
      boolean errorOnInitiate;
      boolean errorOnValidate;
      boolean newEntryContext;
      // Read all invoice lines and validate
      APIBL.setCONO(APIBH.getCONO());
      APIBL.setDIVI().moveLeftPad(APIBH.getDIVI());
      APIBL.setINBN(APIBH.getINBN());
      APIBL.SETLL("00", APIBL.getKey("00", 3));
      while (APIBL.READE("00", APIBL.getKey("00", 3))) {
         // - For each invoice line:
         //   - call maintain_initiate, maintain_validate in APS451Fnc
         // Call APS451Fnc, maintain - step initiate
         // =========================================
         pAPS451Fnc_maintain = get_pAPS451Fnc_maintain();
         pAPS451Fnc_maintain.messages.forgetNotifications();
         pAPS451Fnc_maintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
         pAPS451Fnc_maintain.indicateAutomated();
         // Set primary keys
         // - Division
         pAPS451Fnc_maintain.DIVI.set().moveLeftPad(APIBL.getDIVI());
         // - Invoice batch number
         pAPS451Fnc_maintain.INBN.set(APIBL.getINBN());
         // - Transaction number
         pAPS451Fnc_maintain.TRNO.set(APIBL.getTRNO());
         // Pass a reference to the record.
         pAPS451Fnc_maintain.APIBL = APIBL;
         pAPS451Fnc_maintain.passFAPIBL = true;
         passExtensionTable_lines(); // exit point for modification
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS451Fnc", pAPS451Fnc_maintain);
         LDAZZ.FPNM.move(this.DSPGM);
         // =========================================
         // Handle messages
         errorOnInitiate = validateInvoiceLine_handleMessages(pAPS451Fnc_maintain.messages);
         if (errorOnInitiate) {
            // Add errors to error log
            addToErrorLog_line(pAPS451Fnc_maintain.messages);
         }
         // Release resources allocated by the parameter list.
         pAPS451Fnc_maintain.release();
         if (!errorOnInitiate) {
            // Call APS451Fnc, maintain - step validate
            // =========================================
            do {
               pAPS451Fnc_maintain.prepare(cEnumMode.CHANGE, cEnumStep.VALIDATE);
               // =========================================
               XFPGNM.move(LDAZZ.FPNM);
               LDAZZ.FPNM.move(this.DSPGM);
               apCall("APS451Fnc", pAPS451Fnc_maintain);
               LDAZZ.FPNM.move(this.DSPGM);
               // =========================================
               // Handle messages
               errorOnValidate = validateInvoiceLine_handleMessages(pAPS451Fnc_maintain.messages);
               newEntryContext = pAPS451Fnc_maintain.isNewEntryContext();
               if (errorOnValidate) {
                  // Add errors to error log
                  addToErrorLog_line(pAPS451Fnc_maintain.messages);
               }
               // Release resources allocated by the parameter list.
               pAPS451Fnc_maintain.release();
            } while (newEntryContext);
         }
      }
   }

   /**
   * Returns true if line errors are accumulated to header record
   * @param messages
   *    Container with list of messages
   * @return
   *    true if if line errors are accumulated to header record
   */
   public boolean accumulateLineErr(cCRMessageList messages) {
      int highestLineError = 1;
      if (!APIBH.CHAIN("00", APIBH.getKey("00"))) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else {
         APIBL.setCONO(APIBH.getCONO());
         APIBL.setDIVI().moveLeftPad(APIBH.getDIVI());
         APIBL.setINBN(APIBH.getINBN());
         APIBL.SETLL("00", APIBL.getKey("00", 3));
         while (APIBL.READE("00", APIBL.getKey("00", 3))) {
            if (highestLineError == cRefIBLEext.NO_ERRORS() && 
               (APIBL.getIBLE() == cRefIBLEext.WARNINGS() || 
                APIBL.getIBLE() == cRefIBLEext.ERRORS())) {
               highestLineError = APIBL.getIBLE();
            }
            if ((highestLineError == cRefIBLEext.NO_ERRORS() || 
               highestLineError == cRefIBLEext.WARNINGS()) && 
               APIBL.getIBLE() == cRefIBLEext.ERRORS()) {
               highestLineError = cRefIBLEext.ERRORS();
            }
         }
      }
      if (APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
         APIBH.setIBLE(highestLineError);
         APIBH.UPDAT("00");
      }
      return true;
   }

   /**
   * Operation reversePrintout.
   */
   public void do_reversePrintout() {
      // Validate input parameters
      // =========================================
      // Division
      pReversePrintout.DIVI.validateMANDATORYandConstraints();
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pReversePrintout.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pReversePrintout.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Supplier invoice batch number
      pReversePrintout.INBN.validateMANDATORYandConstraints();

      // Return error messages
      if (pReversePrintout.messages.existError()) {
         return;
      }
      // Perform the operation
      // =========================================
      executeTransaction(pReversePrintout.getTransactionName());
   }

   /**
   * Perform explicit transaction APS450FncOPreversePrintout
   */
   @Transaction(name=cPXAPS450FncOPreversePrintout.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncOPreversePrintout() {
      // Declaration
      boolean alreadyLocked = false;
      // Check if reverse printout is possible
      // =========================================
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().moveLeftPad(pReversePrintout.DIVI.get());
      APIBH.setINBN(pReversePrintout.INBN.get());
      if (!lockForReversePrintout(pReversePrintout.messages, alreadyLockedByJob)) {
         return;
      }
      alreadyLocked = alreadyLockedByJob.getBoolean();
      // Lock FAPIBH for update
      if (!APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         pReversePrintout.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return;
      }
      APIBH.setSUPA(cRefSUPAext.VALIDATED());
      FAPIBH_setChanged();
      APIBH.UPDAT("00");
      // Unlock FAPIBH
      if (!alreadyLocked) {
      unlock(pReversePrintout.messages);
   }

   }

  /**
   * Operation resetToStatusNew.
   */
   public void do_resetToStatusNew() {
      // Validate input parameters
      // =========================================
      // Division
      pResetToStatusNew.DIVI.validateMANDATORYandConstraints();
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pResetToStatusNew.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pResetToStatusNew.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Invoice batch number
      pResetToStatusNew.INBN.validateMANDATORYandConstraints();
      // Return error messages
      if (pResetToStatusNew.messages.existError()) {
         return;
      }
      // Perform the operation
      // =========================================
      executeTransaction(pResetToStatusNew.getTransactionName());
   }

  /**
   * Perform explicit transaction APS450FncOPresetToStatusNew
   */
   @Transaction(name=cPXAPS450FncOPresetToStatusNew.LOGICAL_NAME, primaryTable="FAPIBH") 
   public void transaction_APS450FncOPresetToStatusNew() {
      // Perform the operation
      // =========================================
      // Chain batch invoice
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().move(pResetToStatusNew.DIVI.get());
      APIBH.setINBN(pResetToStatusNew.INBN.get());
      // Set status to 10-New
      resetToStatusNew(pResetToStatusNew.messages);
   }

   /**
   * Returns true if supplier invoice batch number is reset to status 10 - NEW
   * @param messages
   *    Container with list of messages
   * @return
   *    true if if supplier invoice batch number is reset to status 10 - NEW
   */
   public boolean resetToStatusNew(cCRMessageList messages) {
      boolean found_FAPIBH = false;
      boolean lockedInExplicitTransaction = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // Lock FAPIBH for update
      if (activeTrans) {
         // Explicit transactions are turned on
         int status = APIBH.TRY_CHAIN_LOCK("00", APIBH.getKey("00"));
         found_FAPIBH = status != 0;
         lockedInExplicitTransaction = status == -1;
      } else {
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      }
      // Check if locked
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Batch invoice number &1 does not exist
         messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
         return false;
      } else if (lockedInExplicitTransaction) {
         // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
         messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
         messageData_AP_0028_CHID.moveLeftPad("-"); // User locking is unknown.
         messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
         return false;
      } else {
         // Record is locked
         if (APIBH.getSUPA() > cRefSUPAext.APPROVED()) {
            // X__7799 = Reset to status new
            messageData_X_00051_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("X__7799", "MVXCON")));
            messageData_X_00051_OPT2.moveLeftPad("23");
            messageData_X_00051_SUPA.moveLeftPad(CRCommon.formatNumForMsg(APIBH.getSUPA()));
            // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
            pResetToStatusNew.messages.addError(this.DSPGM, "INBN", "X_00051", messageData_X_00051);
            APIBH.UNLOCK("00");
            return false;
         } else {
            if (APIBH.getBIST() != cRefBISText.THE_INVOICE_CAN_BE_PROCESSED() &&
               !checkIfStuckWIP(APIBH.getJNA(), APIBH.getCHID(), APIBH.getJNU()))
            {
               // The invoice is work in progress by an active job.
                  // MSGID=AP_0028 Invoice batch number &1 has the status of work in progress. User &2.
               messageData_AP_0028_INBN.moveLeft(APIBH.getINBN(), 15);
               messageData_AP_0028_CHID.moveLeftPad(APIBH.getCHID());
               messages.addError(this.DSPGM, "INBN", "AP_0028", messageData_AP_0028);
               APIBH.UNLOCK("00");
               return false;
            } else {
               // The invoice is NOT work in progress,
               // or the invoice is work in progress by an inactive job (i.e. it is stuck),
               // or the invoice is work in progress by the current job (i.e. it is stuck).
               APIBH.setBIST(cRefBISText.THE_INVOICE_CAN_BE_PROCESSED()); // Clear work in progress flag if invoice stuck
               APIBH.setSUPA(cRefSUPAext.NEW());
               APIBH.setJNU().move(this.DSJNU);
               APIBH.setJNA().move(this.DSJNA);
               APIBH.setLMDT(movexDate());
               APIBH.setCHID().move(this.DSUSS);
               APIBH.setCHNO(APIBH.getCHNO() + 1);
               APIBH.UPDAT("00");
               return true;
            }
         }
      }
   }

   /**
   * Returns fiscal year from date
   * @param date
   *    The date from where year is retrieved in format YYYYMMDD.
   * @param DIVI
   *    Division
   * @param return
   *    Fiscal Year for the parameter date.
   *    If invalid date or period type for FIM not set in CMNDIV, zero is returned.
   */
   public int getYearFromDate(int date, MvxString DIVI) {
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, DIVI);
      if (CRCalendar.lookUpDate(currentCONO, DIVI, date)) {
         return CRCalendar.getFiscalYear(MNDIV.getPTFA());
      } else {
         return 0;
      }
   }

   /**
   * Sets fields LMDT, CHID, CHNO for the record.
   */
   public void FAPIBH_setChanged() {
      APIBH.setLMDT(movexDate());
      APIBH.setCHID().move(this.DSUSS);
      APIBH.setCHNO(APIBH.getCHNO() + 1);
   }

   /**
   * Moves the record from one DB-interface to another.
   * @param to
   *    The DB-interface to move data to.
   * @param from
   *    The DB-interface to move data from.
   */
   public void moveFAPIBH(mvx.db.dta.FAPIBH to, mvx.db.dta.FAPIBH from) {
      FAPIBH_record.reset();
      FAPIBH_record.setMDBRecord(from);
      FAPIBH_record.reset();
      FAPIBH_record.getMDBRecord(to);
   }

   /**
   * Returns true if FAM function is found
   * @return true if FAM function is found
   */
   public boolean getFamFunction() {
      // Retrieve voucher series no
      if (famFunction_CONO == currentCONO &&
          famFunction_DIVI.EQ(pMaintain.DIVI.get()) &&
          famFunction_SUNO.EQ(pMaintain.SUNO.get()) &&
          famFunction_SDAP.EQ(pMaintain.SDAP.get()) &&
          famFunction_IBTP.EQ(pMaintain.IBTP.get())) {
      // Do not search if already correct selection
      } else {
         famFunction_CONO = currentCONO;
         famFunction_DIVI.move(pMaintain.DIVI.get());
         famFunction_SUNO.move(pMaintain.SUNO.get());
         famFunction_SDAP.move(pMaintain.SDAP.get());
         famFunction_IBTP.move(pMaintain.IBTP.get());
         value_FEID.move("AP50");
         // Set FAM-function
         switch (0) {
            default:
               if (pMaintain.IBTP.get().EQ(cRefIBTPext.SELF_BILLING())) {
                  // From MPFAMF (PPS114) if self billing
                  PFAMF.setCONO(currentCONO);
                  PFAMF.setDIVI().move(pMaintain.DIVI.get());
                  PFAMF.setFEID().clear();
                  PFAMF.setSUNO().clear();
                  PFAMF.setFEID().move("AP52");
                  PFAMF.setSUNO().move(pMaintain.SUNO.get());
                  IN91 = !PFAMF.CHAIN("00", PFAMF.getKey("00", 4));
                  if (!IN91) {
                     value_FNCN.moveLeft(PFAMF.getFNCN(), cRefFNCN.length());
                     value_FEID.move("AP52");
                  } else {
                     PFAMF.setSUNO().clear();
                     IN91 = !PFAMF.CHAIN("00", PFAMF.getKey("00", 4));
                     if (!IN91) {
                        value_FNCN.moveLeft(PFAMF.getFNCN(), cRefFNCN.length());
                        value_FEID.move("AP52");
                     } else {
                        value_FEID.move("AP50");
                        value_FNCN.moveLeft(1, cRefFNCN.length());
                     }
                  }
                  break;
               }
               if (pMaintain.IBTP.get().EQ(cRefIBTPext.DEBIT_NOTE())) {
                  // Debit note
                  value_FEID.move("AP51");
                  // Read AP Standard document
                  found_CSYTAB_SDAP = cRefSDAPext.getCSYTAB_SDAP(SYTAB, found_CSYTAB_SDAP, currentCONO, pMaintain.DIVI.get(), pMaintain.SDAP.get());
                  cRefSDAPext.setDSSDAP(SYTAB, DSSDAP);
                  if (DSSDAP.getYUFNCN() != 0) {
                     value_FNCN.moveLeft(DSSDAP.getYUFNCN(), cRefFNCN.length());
                  } else {
                     value_FNCN.moveLeft(1, cRefFNCN.length());
                  }
                  break;
               }
               if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
                  // Pre payment - pre invoice
                  value_FEID.move("AP53");
                  value_FNCN.moveLeft(1, cRefFNCN.length());
                  break;
               }
               if (pMaintain.IBTP.get().EQ(cRefIBTPext.PREPAYMENT_FINAL_INVOICE())) {
                  // Pre payment - final invoice
                  value_FEID.move("AP54");
                  value_FNCN.moveLeft(1, cRefFNCN.length());
                  break;
               }
               value_FEID.move("AP50");
               value_FNCN.moveLeft(1, cRefFNCN.length());
               break;
         }
         value_FFNC.moveLeftPad(value_FEID);
         value_FFNC.move(value_FNCN);
         SYTAB.setSTKY().moveLeftPad(value_FFNC);
         found_CSYTAB_FFNC = cRefFFNCext.getCSYTAB_FFNC(SYTAB, found_CSYTAB_FFNC, currentCONO, pMaintain.DIVI.get(), SYTAB.getSTKY());
         cRefFFNCext.setDSFFNC(SYTAB, DSFFNC);
         if (!found_CSYTAB_FFNC) {
            XXAPCH = 0;
            XXCDGN = 0;
            XXVSER.clear();
            return false;
         }
      }
      XXAPCH = DSFFNC.getDFAPCH();
      XXCDGN = DSFFNC.getDFCDGN();
      XXVSER.moveLeft(DSFFNC.getDFVSER());
      // Check if FAM function need to be overriden 	
      retrieveNewFamFuntion();
      return true;
   }

   /**
   *  Check if FAM function should be overriden
   *  Overrides VSER, CDGR and APCH
   */
   public void retrieveNewFamFuntion(){ 	
      PLRTVFNC.ETCONO = currentCONO; 	 	
      PLRTVFNC.ETPGNM.moveLeftPad("APS450"); 	 	
      PLRTVFNC.ETDIVI.move(pMaintain.DIVI.get()); 	 	
      PLRTVFNC.ETFEID.move(value_FEID);	 	
      PLRTVFNC.ETACDT = 0; 	 	
      PLRTVFNC.ETCUNO.clear(); 	 	
      PLRTVFNC.ETSUNO.move(pMaintain.SUNO.get()); 	 	
      PLRTVFNC.ETFTCO.move(pMaintain.FTCO.get()); 	 	
      PLRTVFNC.ETBSCD.move(pMaintain.BSCD.get()); 	 	
      PLRTVFNC.ETECAR.clear(); 	 	
      PLRTVFNC.ETEUVT = PLCHKIF.FTEUVT; 	 	
      PLRTVFNC.ETFOTA = 0; 	 	
      if (greaterOrEquals(pMaintain.CUAM.get(), cRefCUAM.decimals(), 0d, cRefCUAM.decimals())){ 	
         PLRTVFNC.ETCRED = 0; 	 	
      } else { 	
         PLRTVFNC.ETCRED = 1; 	 	
      } 	
      PLRTVFNC.CRTVFNC(); 	 	
      if (PLRTVFNC.ETFOTA == 1) { 	 	
         // Set values from FAM function exeptions 	 	
         XXVSER.move(PLRTVFNC.ETVSER); 	 	
         XXCDGN = PLRTVFNC.ETCDGN; 	 	
         XXAPCH = PLRTVFNC.ETAPCH; 	 	
      } 	
    }

   /**
   * Init the cash discount for a new invoice.
   */
   public void initCashDiscount(double CUAM, double VTAM) { 	
      // Reset variables
      double cashDiscAmount;
      pMaintain.CDT1.clearValue();
      pMaintain.CDT2.clearValue();
      pMaintain.CDT3.clearValue();
      pMaintain.CDP1.clearValue();
      pMaintain.CDP2.clearValue();
      pMaintain.CDP3.clearValue();
      pMaintain.CDC1.clearValue();
      pMaintain.CDC2.clearValue();
      pMaintain.CDC3.clearValue();
      // Calculate cash discount dates and amounts 	
      if (!pMaintain.TECD.isBlank()) {
         getFamFunction();
         found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, found_CIDMAS, currentCONO, pMaintain.SPYN.get());
         found_CSYTAB_TECD = cRefTECDext.getCSYTAB_TECD(SYTAB, found_CSYTAB_TECD, currentCONO, pMaintain.TECD.get(), IDMAS.getLNCD());
         if (found_CSYTAB_TECD) { 	
            cRefTECDext.setDSTECD(SYTAB, DSTECD);
            pMaintain.CDT1.set(getBankDay(DSTECD.getCDCDDY())); 	
            pMaintain.CDT2.set(getBankDay(DSTECD.getCDCDD2())); 	
            pMaintain.CDT3.set(getBankDay(DSTECD.getCDCDD3())); 	
            // Gross or Net amount for cash discount calculation 	
            cashDiscAmount = CUAM; 	
            if (XXCDGN == 2) { 	 	
               cashDiscAmount -= VTAM; 	
            } 	
            // Calculate cash discount amounts 	
            if (!equals(DSTECD.getCDCDPC(), 0d, cRefCDPC.decimals())) { 	
               pMaintain.CDC1.set((cashDiscAmount * DSTECD.getCDCDPC())/100d); 	
               pMaintain.CDP1.set(DSTECD.getCDCDPC()); 	
            } 	
            if (!equals(DSTECD.getCDCDP2(), 0d, cRefCDPC.decimals())) { 	
               pMaintain.CDC2.set((cashDiscAmount * DSTECD.getCDCDP2())/100d); 	
               pMaintain.CDP2.set(DSTECD.getCDCDP2()); 	
            } 	
            if (!equals(DSTECD.getCDCDP3(), 0d, cRefCDPC.decimals())) { 	
               pMaintain.CDC3.set((cashDiscAmount * DSTECD.getCDCDP3())/100d); 	
               pMaintain.CDP3.set(DSTECD.getCDCDP3()); 	
            } 	
         } 	
      } 	
   } 	 	

   /**
   * Get next bank day.
   *
   * @param CDDY the count of Cash Discount date.
   * @return the calculated bank day.
   */
   public int getBankDay(int CDDY) {
      int centuryRelativeDayNumber;
      if (CDDY != 0) {
         if (!CRCalendar.lookUpDate(currentCONO, pMaintain.DIVI.get(), pMaintain.IVDT.get())) {
            // Error
            return 0;
         }
         centuryRelativeDayNumber = CRCalendar.getCenturyDayNo();
         centuryRelativeDayNumber += CDDY;
         if (!CRCalendar.convertCenturyDayNo(currentCONO, pMaintain.DIVI.get(), centuryRelativeDayNumber)) {
            // Error
            return 0;
         }
         if (CRCalendar.isBankDay()) {
            return CRCalendar.getDate();
         }
         while (!CRCalendar.isBankDay()) {
            centuryRelativeDayNumber ++;
            if (!CRCalendar.convertCenturyDayNo(currentCONO, pMaintain.DIVI.get(), centuryRelativeDayNumber)) {
               // Error
               return 0;
            }
            if (CRCalendar.isBankDay()) {
               return CRCalendar.getDate();
            } 	
         } 	
      } 	
      return 0; 	
   } 	 	

   /**
   * Sets from/to country code on the invoice depending on if a PO is
   * connected. If a VRNO is sent the two first positions should be used
   * to check if it's a country. This is used to set FTCO if no PO is
   * connected or if PO is connected and country is equal to base country
   * Else take from final delivery address in CRS622
   * In other cases value from CIDMAS is used
   *
   *  @param PUNO Purchase order number
   */
   public void setFromToCountry(MvxString PUNO) {
      //    Invoice without PO
      if (PUNO.isBlank()) {
         if (!pMaintain.VRNO.isBlank()) {
            XXVRNO.moveLeftPad(pMaintain.VRNO.get());
            if (hasCountryCode(XXVRNO)) {
               pMaintain.FTCO.set().moveLeftPad(XXCSCD);
               return;
            }
         }
      } else {
         if (!pMaintain.VRNO.isBlank()) {
            XXVRNO.moveLeftPad(pMaintain.VRNO.get());
            if (hasCountryCode(XXVRNO)) {
               if (XXCSCD.EQ(pMaintain.BSCD.get())) {
                  pMaintain.FTCO.set().moveLeftPad(pMaintain.BSCD.get());
                  return;
               }
            }
         }
      }
      IDADR.setCONO(currentCONO);
      IDADR.setSUNO().moveLeftPad(pMaintain.SUNO.get());
      IDADR.setADTE(5);
      if (IDADR.CHAIN("00", IDADR.getKey("00", 3))) {
         pMaintain.FTCO.set().moveLeftPad(IDADR.getCSCD());
         return;
      }
      found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, found_CIDMAS, currentCONO, pMaintain.SUNO.get());
      pMaintain.FTCO.set().moveLeftPad(IDMAS.getCSCD());
   }

   /**
   * Sets base country code on the invoice depending on if a PO is
   * connected. If a PO is connected base code is set
   * from fiscal rep if it exists for the country of the warehouse on the PO.
   * In other cases value from divison(CMNDIV) is used
   * @param PUNO Purchase order number
   */
   public void setBaseCountry(MvxString PUNO) {
      // Invoice without PO
      if (!pMaintain.PUNO.isBlank()) {
         //--------------------------------------------------------------------------
         found_MPHEAD = cRefPUNOext.getMPHEAD(PHEAD, found_MPHEAD, currentCONO, pMaintain.PUNO.get());
         found_MITWHL = cRefWHLOext.getMITWHL(ITWHL, found_MITWHL, currentCONO, PHEAD.getWHLO());
         found_CSYTAB_CSCD = cRefCSCDext.getCSYTAB_CSCD(SYTAB, found_CSYTAB_CSCD, currentCONO, ITWHL.getCSCD());
         if (found_MPHEAD && found_MITWHL && found_CSYTAB_CSCD) {
            cRefCSCDext.setDSCSCD(SYTAB, DSCSCD);
            if (!DSCSCD.getYHVRIN().isBlank()) {
               pMaintain.BSCD.set().moveLeftPad(ITWHL.getCSCD());
               return;
            }
         }
      }
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.DIVI.get());
      if (found_CMNDIV) {
         pMaintain.BSCD.set().moveLeftPad(MNDIV.getCSCD());
      }
   }

  /**
   * hasCountryCode returns true if first two positions in VRNO
   * is a country code
   *
   * @param VRNO VAT Registration number
   * @return true if first two position are a country code
   */
   public boolean hasCountryCode(MvxString VRNO) {
      // If it starts with numeric value, this is no ISO country code
      if (testNumeric(XXCSCD)) {
         return false;
      }
      // Check if the VAT-regno. starts with a country code
      found_CSYTAB_CSCD = cRefCSCDext.getCSYTAB_CSCD(SYTAB, found_CSYTAB_CSCD, currentCONO, XXCSCD);
      if (found_CSYTAB_CSCD) {
         return true;
      } else {
         // If not found, try to right justify the country code " SE"
         value_STKY.moveLeftPad(" " + XXCSCD.toString());
         found_CSYTAB_CSCD = cRefCSCDext.getCSYTAB_CSCD(SYTAB, found_CSYTAB_CSCD, currentCONO, value_STKY);
         if (found_CSYTAB_CSCD) {
            return true;
         }
      }
      return false;
   }

  /**
   * Returns the bank ID with the highest priority for the payee/supplier
   *
   * @param  DIVI    Division
   * @param  ACHO    Account holder (Payee/Supplier)
   * @return found bank Id or blank
   */
   public MvxString getBankId(MvxString DIVI, MvxString ACHO) {
      returnValueBKID.clear();
      tempSTAT.clear();
      found_CBANAC = false;
      //   Find bank-ID with highest priority
      BANAC.setCONO(currentCONO);
      BANAC.setDIVI().clear();
      BANAC.setBKTP(3);
      BANAC.setACHO().moveLeftPad(ACHO);
      BANAC.setCBPY(getIntMax(2));
      BANAC.setBKID().setMax();
      BANACselection = setSelectionCBANAC70_e90();          	 	
      BANAC.SETGT("70", BANAC.getKey("70"));
      if (BANAC.REDPE("70", BANAC.getKey("70", 4), BANACselection)) { 
         //   Take first bank-ID (alpha) from that priority 	
         BANAC.SETLL("70", BANAC.getKey("70", 5)); 	
         if (BANAC.READE("70", BANAC.getKey("70", 5), BANACselection)) { 
            returnValueBKID.moveLeftPad(BANAC.getBKID()); 	
            tempSTAT.move(BANAC.getSTAT()); 	
         } 	
      } else { 	
         BANAC.SETGT("70", BANAC.getKey("70")); 	
         if (BANAC.REDPE("70", BANAC.getKey("70", 4))) {
            //   Take first bank-ID (alpha) from that priority
            BANAC.SETLL("70", BANAC.getKey("70", 5)); 	
            if (BANAC.READE("70", BANAC.getKey("70", 5))) { 	
               returnValueBKID.moveLeftPad(BANAC.getBKID());
               tempSTAT.move(BANAC.getSTAT()); 	
            } 	
         } 	
      }
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      if (MNCMP.getCMTP() == 2) {      
         //   Find bank-ID with highest priority 	
         BANAC.setCONO(currentCONO); 	
         BANAC.setDIVI().moveLeftPad(DIVI); 	
         BANAC.setBKTP(3); 	
         BANAC.setACHO().move(ACHO); 	
         BANAC.setCBPY(getIntMax(2));
         BANAC.setBKID().setMax();
         BANAC.SETGT("70", BANAC.getKey("70")); 	
         if (BANAC.REDPE("70", BANAC.getKey("70", 4), BANACselection)) { 	
            //    Take first bank-ID (alpha) from that priority 	
            BANAC.SETLL("70", BANAC.getKey("70", 5)); 	
            if (BANAC.READE("70", BANAC.getKey("70", 5), BANACselection)) { 	
               if (tempSTAT.EQ("90") && BANAC.getSTAT().EQ("20") || 	
                  tempSTAT.EQ(BANAC.getSTAT())) { 	
                  returnValueBKID.moveLeftPad(BANAC.getBKID()); 	
               } 
            }    	
         } else { 	
            BANAC.SETGT("70", BANAC.getKey("70"));
            if (BANAC.REDPE("70", BANAC.getKey("70", 4))) {
               //   Take first bank-ID (alpha) from that priority
               BANAC.SETLL("70", BANAC.getKey("70", 5)); 	
               if (BANAC.READE("70", BANAC.getKey("70", 5))) { 	
                  if (tempSTAT.EQ("90") && BANAC.getSTAT().EQ("20") || 	
                     tempSTAT.EQ(BANAC.getSTAT())) { 	
                     returnValueBKID.moveLeftPad(BANAC.getBKID());
                  }
               }
            }
         }
      }
      return returnValueBKID;
   }

  /*
   * Creates rounding transaction if needed
   */
   public void createRoundingTransaction() {
      double evaluateAmount = 0d; 	
      double varianceAmount = 0d; 	
      boolean found_FAPIBH = false;
      int highTRNO = 0;
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, currentCONO, pValidateInv.DIVI.get(), pValidateInv.INBN.get());
      if (found_FAPIBH) {
         evaluateAmount = (APIBH.getCUAM() - (APIBH.getTLNA() + APIBH.getVTAM()));
         if (!equals(evaluateAmount, 2, 0d)) {
            foundParam_CIDVEN.moveLeft(toChar(found_CIDVEN));
            foundParam_CSUDIV.moveLeft(toChar(found_CSUDIV));             
            if (APIBH.getSPYN().isBlank()) { 	
               cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, APIBH.getCONO(), APIBH.getDIVI(), APIBH.getSUNO());
            } else {
               cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, APIBH.getCONO(), APIBH.getDIVI(), APIBH.getSPYN());
            }
            found_CIDVEN = foundParam_CIDVEN.getBoolean();
            found_CSUDIV = foundParam_CSUDIV.getBoolean();
            //   Check if left to distribute is within the allowable variance 	
            GINPA.setCONO(APIBH.getCONO()); 	
            GINPA.setDIVI().move(APIBH.getDIVI()); 	
            GINPA.setINIO(toChar(5)); 	
            GINPA.setOBV1().moveLeftPad(APIBH.getCUCD()); 	
            GINPA.setOBV2().moveLeftPad(IDVEN.getSUCL()); 	
            GINPA.setOBV3().moveLeftPad(APIBH.getSUNO());
            found_FGINPA = GINPA.CHAIN("00", GINPA.getKey("00", 6));            
            if (!found_FGINPA) {
               GINPA.setOBV3().clear();
               found_FGINPA = GINPA.CHAIN("00", GINPA.getKey("00", 6));
            }
            if (!found_FGINPA) {
               GINPA.setOBV2().clear();
               found_FGINPA = GINPA.CHAIN("00", GINPA.getKey("00", 6));
            }
            if (found_FGINPA) {
               varianceAmount = mvxHalfAdjust((double)evaluateAmount * GINPA.getPDEI(), 2); 	
               varianceAmount = mvxHalfAdjust((double)varianceAmount/100d, 4);	 	
               if (greaterOrEquals(GINPA.getPDEI(), 2, varianceAmount) && 	
                   greaterOrEquals(GINPA.getDEAI(), 2, evaluateAmount) && 	
                   greaterThan(varianceAmount, 4, 0d) && 	
                   greaterThan(evaluateAmount, 2, 0d)) { 	
                  // Create line type 4 if within the allowable variance 	
                  //  Fetch next transaction number
                  APIBL.setCONO(APIBH.getCONO());
                  APIBL.setDIVI().moveLeftPad(APIBH.getDIVI());
                  APIBL.setINBN(APIBH.getINBN());
                  APIBL.SETGT("00", APIBL.getKey("00", 3));
                  if (!APIBL.REDPE("00", APIBL.getKey("00", 3))) {
                     highTRNO = 1;
                  } else {
                     highTRNO = APIBL.getTRNO() + 1;
                  }
                  APIBL.setCONO(APIBH.getCONO());
                  APIBL.setDIVI().moveLeftPad(APIBH.getDIVI());
                  APIBL.setINBN(APIBH.getINBN());
                  APIBL.setTRNO(highTRNO);
                  if (!APIBL.CHAIN("00", APIBL.getKey("00"))) {
                     APIBL.clearNOKEY("00");
                     APIBL.setRDTP(cRefRDTPext.ROUNDING_OFF()); 	
                     APIBL.setNLAM(evaluateAmount); 	
                     APIBL.setPUNO().moveLeftPad(APIBH.getPUNO()); 	
                     APIBL.setRGDT(movexDate()); 	
                     APIBL.setLMDT(APIBL.getRGDT()); 	
                     APIBL.setCHID().move(this.DSUSS); 	
                     APIBL.setRGTM(movexTime()); 	
                     APIBL.WRITE("00");    	
                     if (APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
                        APIBH.setTLNA(APIBH.getTLNA() + evaluateAmount);
                        APIBH.UPDAT("00");
                     }
                  } 	
               } 	
            }
         }
      }
   }

  /**
    * Set selection for CBANAC70, STAT != 90
    */
   public FieldSelection setSelectionCBANAC70_e90() {    	 	
      BANACselection = null;   	 	
      Expression exp = Expression.createNE("CBANAC", "BCSTAT", String.valueOf(90));           	 	
      FieldSelection fs = new FieldSelection("CBANAC", "70");         	 	
      fs.setExpression(exp);         	 	
      return fs;      	 	
   }   	 	

  /*
   * Add errors found on the order head to the error log.
   * Pre condition: FAPIBH is read.
   */
   public void addToErrorLog_head(cCRMessageList messages) {
      writeMailHead();
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         // Write message in CRMessageDS to error log
         CRS428DS.setCRS428DS().clear();
         CRS428PDS.setCRS428PDS().clear();
         CRS428DS.setC1CONO(currentCONO);
         CRS428DS.setC1REC2().move(APIBH.getAPCD());
         CRS428DS.setC1ADAT(movexDate());
         CRS428DS.setC1MLID(XXMLID);
         CRS428DS.setC1DTID(APIBH.getDTID());
         CRS428DS.setC1DTRF(0);
         CRS428DS.setC1PGNM().moveLeft("APS450");
         CRS428DS.setC1OPC().moveLeftPad("*CRTDML");
         CRS428DS.setC1MSID().move(CRMessageDS.getPXMSID());
         if (CRMessageDS.getPXTYPE() == 'N') {
            CRS428DS.setC1LEVL(10);
         } else {
            CRS428DS.setC1LEVL(40);
         }
         CRS428DS.setC1MSGD().moveLeftPad(CRMessageDS.getPXMSGD());
         CRS428PDS.setC2CONO(currentCONO);
         CRS428PDS.setC2PGNM().moveLeftPad("APS450");
         CRS428PDS.setC2FILE().moveLeftPad("FAPIBH");
         String keyFields = getPrimaryKeyForTable("FAPIBH");
         moveToArray(KSTR,0,keyFields);
         CRS428PDS.setC2KSTR().moveLeftPad(KSTR);
         CRS428preCall();
         apCall("CRS428", pParameters);
         CRS428postCall();
      }
   }

   /*
   * Add errors found on the order line to the error log.
   * Pre condition: FAPIBH, FAPIBL is read.
   */
   public void addToErrorLog_line(cCRMessageList messages) {
      if (!mailHeadWritten) {
         writeMailHead();
      }
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         // Write message in CRMessageDS to error log
         CRS428DS.setCRS428DS().clear();
         CRS428PDS.setCRS428PDS().clear();
         CRS428DS.setC1CONO(APIBH.getCONO());
         CRS428DS.setC1REC2().move(APIBH.getAPCD());
         CRS428DS.setC1ADAT(movexDate());
         CRS428DS.setC1MLID(XXMLID);
         CRS428DS.setC1DTID(APIBH.getDTID());
         CRS428DS.setC1DTRF(APIBL.getTRNO());
         CRS428DS.setC1PGNM().moveLeft("APS451");
         CRS428DS.setC1OPC().moveLeftPad("*CRTDML");
         CRS428DS.setC1MSID().move(CRMessageDS.getPXMSID());
         if (CRMessageDS.getPXTYPE() == 'N') {
            CRS428DS.setC1LEVL(10);
         } else {
            CRS428DS.setC1LEVL(40);
         }
         CRS428DS.setC1MSGD().moveLeftPad(CRMessageDS.getPXMSGD());
         CRS428PDS.setC2CONO(APIBH.getCONO());
         CRS428PDS.setC2PGNM().moveLeftPad("APS451");
         CRS428PDS.setC2PICC().moveLeft("BI");
         CRS428PDS.setC2FILE().moveLeftPad("FAPIBL");
         String keyFields = getPrimaryKeyForTable("FAPIBL");
         moveToArray(KSTR,0,keyFields);
         CRS428PDS.setC2KSTR().moveLeftPad(KSTR);
         CRS428preCall();
         apCall("CRS428", pParameters);
         CRS428postCall();
      }
   }

  /*
   * Writes the mail head for the error log in CMAILB.
   * Pre condition: FAPIBH is read.
   */
   public void writeMailHead() {
      mailHeadWritten = true;
      // Create record in CMAILB
      CRS428DS.setCRS428DS().clear();
      CRS428PDS.setCRS428PDS().clear();
      XXMLID = 0l;
      // Move fields to parameter list
      CRS428DS.setC1CONO(APIBH.getCONO());
      CRS428DS.setC1REC2().move(APIBH.getAPCD());
      CRS428DS.setC1MTPE().move("156");
      CRS428DS.setC1ADAT(movexDate());
      CRS428DS.setC1PAR1().moveLeft(formatToString(APIBH.getINBN(), 10));
      CRS428DS.setC1PAR2().moveLeftPad(APIBH.getSINO());
      CRS428DS.setC1EMTP().moveLeftPad("04");
      CRS428PDS.setC2QTTP(2);
      CRS428PDS.setC2PGNM().moveLeftPad("APS450");
      CRS428PDS.setC2FILE().moveLeftPad("FAPIBH");
      String keyFields = getPrimaryKeyForTable("FAPIBH");
      moveToArray(KSTR,0,keyFields);
      CRS428PDS.setC2KSTR().moveLeftPad(KSTR);
      CRS428PDS.setC2FL01().moveLeft("XXSINO");
      CRS428PDS.setC2DT01().moveLeftPad(APIBH.getSINO());
      CRS428preCall();
      apCall("CRS428", pParameters);
      CRS428postCall();
      XXMLID = CRS428DS.getC1MLID();
      // Create Data Identity
      if (APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
         APIBH.setDTID(fetchDTID());
         FAPIBH_setChanged();
         APIBH.UPDAT("00");
      }
   }

  /*
   * Fetch the data identity (DTID) from the number series
   */
   public long fetchDTID() {
      this.PXCONO = currentCONO;
      this.PXDIVI.clear();
      PXRTVNBR.PXNBTY.move("44");
      PXRTVNBR.PXNBID = 'D';
      PXRTVNBR.PXNERR = '1';
      IN92 = PXRTVNBR.CRTVNBR();
      if (PXRTVNBR.PXNERR == '0') {
         return PXRTVNBR.PXNBNR;
      } else {
         return 0l;
      }
   }

  /*
   * Clears the error log connected to the DTID.
   * Pre condition: FAPIBH is read.
   */
   public void clearErrorLog() {
      mailHeadWritten = false;
      // Delete existing record in CMAILB
      if (APIBH.getDTID() != 0) {
         MAILP.setCONO(APIBH.getCONO());
         MAILP.setDTID(APIBH.getDTID());
         IN91 = !MAILP.CHAIN("10", MAILP.getKey("10", 2));
         if (!IN91) {
            MAILB.setCONO(APIBH.getCONO());
            MAILB.setMLID(MAILP.getMLID());
            IN91 = !MAILB.CHAIN_LOCK("00", MAILB.getKey("00"));
            if (!IN91) {
               MAILB.DELET("00");
            }
            // Delete existing record in CMAILM and CMAILP
            MAILM.setCONO(APIBH.getCONO());
            MAILM.setDTID(APIBH.getDTID());
            MAILM.SETLL("00", MAILM.getKey("00", 2));
            while (MAILM.READE_LOCK("00", MAILM.getKey("00", 2))) {
               MAILM.DELET("00");
               // Delete from CMAILP
               MAILP.setCONO(MAILM.getCONO());
               MAILP.setDTID(MAILM.getDTID());
               MAILP.setDTRF(MAILM.getDTRF());
               MAILP.setDTIS(MAILM.getDTIS());
               if (MAILP.CHAIN_LOCK("10", MAILP.getKey("10", 4))) {
                  MAILP.DELET("10");
               }
            }
         }
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   * @return
   *    Returns true if an error occured. If errors are received during step
   * initiate, then it is not allowed to proceed with steps validate and update.
   */
   public boolean validateInvoice_handleMessages(cCRMessageList messages) {
      // Declaration
      boolean error = false;
      boolean notification = false;
      // handle messages
      if (messages.exist()) {
         // Check for errors relating to the given panel.
         messages.prepareGetNext();
         if (messages.getNextError(CRMessageDS)) {
            error = true;
         }
         // Check for notifications relating to the given panel.
         if (!error) {
            messages.prepareGetNext();
            if (messages.getNextNotification(CRMessageDS)) {
               notification = true;
            }
         }

      }
      // Set error code
      if (APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
         if (error || notification) {
            // Save Message
            APIBH.setMSID().moveLeftPad(CRMessageDS.getPXMSID());
            APIBH.setMSGD().moveLeftPad(CRMessageDS.getPXMSGD());
         }
         if (error) {
            APIBH.setIBHE(cRefIBHEext.ERRORS());
         } else {
            if (notification) {
               APIBH.setIBHE(cRefIBHEext.WARNINGS());
            } else {
               APIBH.setIBHE(cRefIBHEext.NO_ERRORS());
               // clear Message
               APIBH.setMSID().clear();
               APIBH.setMSGD().clear();
            }
         }
         FAPIBH_setChanged();
         APIBH.UPDAT("00");
      }
      return error;
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   * @return
   *    Returns true if an error occured. If errors are received during step
   * initiate, then it is not allowed to proceed with steps validate and update.
   */
   public boolean validateInvoiceLine_handleMessages(cCRMessageList messages) {
      // Declaration
      boolean error = false;
      boolean notification = false;
      // handle messages
      if (messages.exist()) {
         // Check for errors relating to the given panel.
         messages.prepareGetNext();
         if (messages.getNextError(CRMessageDS)) {
            error = true;
         }
         // Check for notifications relating to the given panel.
         if (!error) {
            messages.prepareGetNext();
            if (messages.getNextNotification(CRMessageDS)) {
               notification = true;
            }
         }
      }
      // Set error code
      if (APIBL.CHAIN_LOCK("00", APIBL.getKey("00"))) {
         if (error || notification) {
            // Save Message
            APIBL.setMSID().moveLeftPad(CRMessageDS.getPXMSID());
            APIBL.setMSGD().moveLeftPad(CRMessageDS.getPXMSGD());
         }
         if (error) {
            APIBL.setIBLE(cRefIBLEext.ERRORS());
         } else {
            if (notification) {
               APIBL.setIBLE(cRefIBLEext.WARNINGS());
            } else {
               APIBL.setIBLE(cRefIBLEext.NO_ERRORS());
               // clear Message
               APIBL.setMSID().clear();
               APIBL.setMSGD().clear();
            }
         }
         APIBL.UPDAT("00");
      }
      return error;
   }

   /**
   * Gets FAPIBH record.
   * @param DIVI
   *    Division
   * @param INBN
   *    Invoice batch number
   * @return
   *    Returns true if FAPIBH record is found.
   */
   public boolean getFAPIBH(MvxString DIVI, long INBN) {
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().move(DIVI);
      APIBH.setINBN(INBN);
      if (!APIBH.CHAIN("00", APIBH.getKey("00"))) {
         APIBH.clearNOKEY("00");
         return false;
      }
      return true;
   }

  /**
   * Returns true if accounting is within date limits and period exist
   */
   public boolean isAccountingDateWithinLimits() {
      if (!getFamFunction()) {
         // MSGID = XFF0010  FAM function &1/&2 is missing in division &3
         messageData_XFF0010_FEID.moveLeftPad(value_FEID);
         messageData_XFF0010_FNCN.moveLeft(value_FNCN);
         MessageData_XFF0010_DIVI.moveLeftPad(pMaintain.DIVI.get());
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
         MessageData_XDT0005_WDFRDT.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), DSFFNC.getDFFRDT(), LDAZD.DTFM, ' '));
         MessageData_XDT0005_WDTODT.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), DSFFNC.getDFTODT(), LDAZD.DTFM, ' '));
         MessageData_XDT0005_WDDATE.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.ACDT.get(), LDAZD.DTFM, ' '));
         //   MSGID=XDT0005 Accounting date &3 is not within the valid range, which is &1 - &2
         errorFieldName.moveLeftPad("ACDT");
         errorMSGID.moveLeftPad("XDT0005");
         errorMessageData.moveLeftPad(MessageData_XDT0005);
         return false;
      }
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, pMaintain.DIVI.get());
      CRCalendar.lookUpDate(currentCONO, pMaintain.DIVI.get(), pMaintain.ACDT.get());
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
      this.PXDIVI.move(pMaintain.DIVI.get());
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
   * Override this method if an extension table has been added 
   * to the pMaintain parameter list as a modification,
   * and needs to be passed in the function program call.
   */
   public void passExtensionTable_lines() {
   }

   /**
   * Set Delivery date based on parameter in CRS750.
   * @param DIVI - Division
   */
   public void setDeliveryDate(MvxString DIVI) {
      // Chain CRS750 - Finance parameters
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      found_CSYPAR_CRS750 = cRefPGNMext.getCSYPAR_PGNM_DIVIorBlank(SYPAR, found_CSYPAR_CRS750, MNCMP.getCMTP(), currentCONO, DIVI, "CRS750");
      CRS750DS.set().moveLeft(SYPAR.getPARM());      
      // Only if VAT is used
      if (MNDIV.getTATM() == 1 || MNDIV.getTATM() == 4) {      
         // Changed key value SPYN give new Delivery date
         if (pMaintain.DEDA.get() == 0) {
            if (CRS750DS.getPBVDTT() == 1) {
               pMaintain.DEDA.set(pMaintain.ACDT.get()); 	
            } 	
            if (CRS750DS.getPBVDTT() == 2) {
               pMaintain.DEDA.set(pMaintain.IVDT.get());               
            } 	
            if (CRS750DS.getPBVDTT() == 3 || 	
                CRS750DS.getPBVDTT() == 4) { 	
               findLatestRCDT(); 	
               // If Receipt date found 	
               if (!isBlank(prevLatestRCDT)) { 	
                  pMaintain.DEDA.set(prevLatestRCDT); 	
               } else { 	
                  if (CRS750DS.getPBVDTT() == 3) {
                     pMaintain.DEDA.set(pMaintain.ACDT.get());                      
                  } 	
                  if (CRS750DS.getPBVDTT() == 4) {
                     pMaintain.DEDA.set(pMaintain.IVDT.get());                     
                  }    	
               } 	
            } 	
            if (CRS750DS.getPBVDTT() == 5) {
               pMaintain.DEDA.set(pMaintain.DUDT.get());               
            } 	
         } 	
      }     
   }

   /**
   * Find the latest Receipt date from <code>MPLINE</code>. <br>
   * If no date found there, use Invoice date to Delivery date.
   *
   * @return <code>Delivery date</code>.
   */
   public int findLatestRCDT() {
      prevLatestRCDT = 0;
      // Get the Delivery day on line level
      if (pMaintain.IMCD.get().EQ("1")) {
         // Set key and select values for reading FAPBIL
         APBIL.setCONO(currentCONO);
         APBIL.setDIVI().moveLeft(pMaintain.DIVI.get());
         APBIL.setSPYN().moveLeft(pMaintain.SPYN.get());
         APBIL.setSUNO().moveLeft(pMaintain.SUNO.get());
         APBIL.setSINO().moveLeft(pMaintain.SINO.get());
         APBIL.SETLL_SCAN("00", APBIL.getKey("00", 5));
         // Select only type 1
         APBIL.setSelection("00", "E2RDTP", "EQ", String.valueOf(1));
         // Exclude all blank PUNO
         APBIL.setSelection("00", "E2PUNO", "NE", String.valueOf(BLANK));
         while (APBIL.READE("00", APBIL.getKey("00", 5))) {            
            // Set keys for reading MPLINE
            PLINE.setCONO(APBIL.getCONO());
            PLINE.setPUNO().moveLeftPad(APBIL.getPUNO());
            PLINE.setPNLI(APBIL.getPNLI());
            PLINE.setPNLS(APBIL.getPNLS());
            if (PLINE.CHAIN("00", PLINE.getKey("00"))) {
               if (PLINE.getRCDT() > prevLatestRCDT) {
                  prevLatestRCDT = PLINE.getRCDT();
               }
            }   
         }
      }
      // Get the Delivery day on header level
      if (pMaintain.IMCD.get().EQ("2")) {
         // Set keys for reading MPLINE
         PLINE.setCONO(currentCONO);
         PLINE.setPUNO().moveLeftPad(pMaintain.PUNO.get());
         PLINE.SETLL_SCAN("00", PLINE.getKey("00", 2));
         // Exclude all records with lower date than the latest we have found
         PLINE.setSelection("00", "IBRCDT", "GT", String.valueOf(prevLatestRCDT));
         while (PLINE.READE("00", PLINE.getKey("00", 2))) {
            if (PLINE.getRCDT() > prevLatestRCDT) {
               prevLatestRCDT = PLINE.getRCDT();
            }
         }
      }      
      // Return a date to Delivery date
      return prevLatestRCDT;
   }    

   /**
   * Initiation of function program
   */
   public void INIT() { 	
      this.PXCONO = currentCONO;
      this.PXDIVI.clear();
      // Create record
      if (FAPIBH_record == null) {
         FAPIBH_record = APIBH.getEmptyRecord();
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
   public mvx.db.dta.FAPIBH APIBH;
   public mvx.db.dta.FAPIBL APIBL;
   public mvx.db.dta.FAPIBA APIBA;
   public mvx.db.dta.FAPIBR APIBR;
   public mvx.db.dta.CIDMAS IDMAS;
   public mvx.db.dta.CIDVEN IDVEN;
   public mvx.db.dta.CSUDIV SUDIV;
   public mvx.db.dta.CSYTAB SYTAB;
   public mvx.db.dta.FCR040 CR040;
   public mvx.db.dta.FPLEDG PLEDG;
   public mvx.db.dta.CMNDIV MNDIV;
   public mvx.db.dta.CSYPAR SYPAR;
   public mvx.db.dta.MPFAMF PFAMF;
   public mvx.db.dta.CIADDR IADDR;
   public mvx.db.dta.FAPRCD APRCD;
   public mvx.db.dta.CFACIL FACIL;
   public mvx.db.dta.MPHEAD PHEAD;
   public mvx.db.dta.MPSUPR PSUPR;
   public mvx.db.dta.MITWHL ITWHL;
   public mvx.db.dta.CMAILB MAILB;
   public mvx.db.dta.CMAILP MAILP;
   public mvx.db.dta.CMAILM MAILM;
   public mvx.db.dta.CMNCMP MNCMP;
   public mvx.db.dta.FPPPAY PPPAY;
   public mvx.db.dta.CIDADR IDADR;
   public mvx.db.dta.CBANAC BANAC;
   public mvx.db.dta.CGEOJU GEOJU;
   public mvx.db.dta.FGINPA GINPA; 	
   public mvx.db.dta.MPAGRS PAGRS;
   public mvx.db.dta.FAPBIL APBIL;
   public mvx.db.dta.MPLINE PLINE;

   public void initMDB() {
      SYSTR = (mvx.db.dta.CSYSTR)getMDB("CSYSTR", SYSTR);
      APIBH = (mvx.db.dta.FAPIBH)getMDB("FAPIBH", APIBH);
      APIBL = (mvx.db.dta.FAPIBL)getMDB("FAPIBL", APIBL);
      APIBR = (mvx.db.dta.FAPIBR)getMDB("FAPIBR", APIBR);
      APIBA = (mvx.db.dta.FAPIBA)getMDB("FAPIBA", APIBA);
      IDMAS = (mvx.db.dta.CIDMAS)getMDB("CIDMAS", IDMAS);
      IDVEN = (mvx.db.dta.CIDVEN)getMDB("CIDVEN", IDVEN);
      SUDIV = (mvx.db.dta.CSUDIV)getMDB("CSUDIV", SUDIV);
      SYTAB = (mvx.db.dta.CSYTAB)getMDB("CSYTAB", SYTAB);
      CR040 = (mvx.db.dta.FCR040)getMDB("FCR040", CR040);
      PLEDG = (mvx.db.dta.FPLEDG)getMDB("FPLEDG", PLEDG);
      MNDIV = (mvx.db.dta.CMNDIV)getMDB("CMNDIV", MNDIV);
      SYPAR = (mvx.db.dta.CSYPAR)getMDB("CSYPAR", SYPAR);
      PFAMF = (mvx.db.dta.MPFAMF)getMDB("MPFAMF", PFAMF);
      IADDR = (mvx.db.dta.CIADDR)getMDB("CIADDR", IADDR);
      APRCD = (mvx.db.dta.FAPRCD)getMDB("FAPRCD", APRCD);
      FACIL = (mvx.db.dta.CFACIL)getMDB("CFACIL", FACIL);
      PHEAD = (mvx.db.dta.MPHEAD)getMDB("MPHEAD", PHEAD);
      PSUPR = (mvx.db.dta.MPSUPR)getMDB("MPSUPR", PSUPR);
      ITWHL = (mvx.db.dta.MITWHL)getMDB("MITWHL", ITWHL);
      MAILB = (mvx.db.dta.CMAILB)getMDB("CMAILB", MAILB);
      MAILP = (mvx.db.dta.CMAILP)getMDB("CMAILP", MAILP);
      MAILM = (mvx.db.dta.CMAILM)getMDB("CMAILM", MAILM);
      MNCMP = (mvx.db.dta.CMNCMP)getMDB("CMNCMP", MNCMP);
      PPPAY = (mvx.db.dta.FPPPAY)getMDB("FPPPAY", PPPAY);
      IDADR = (mvx.db.dta.CIDADR)getMDB("CIDADR", IDADR);
      BANAC = (mvx.db.dta.CBANAC)getMDB("CBANAC", BANAC);
      GEOJU = (mvx.db.dta.CGEOJU)getMDB("CGEOJU", GEOJU);
      GINPA = (mvx.db.dta.FGINPA)getMDB("FGINPA", GINPA); 	
      PAGRS = (mvx.db.dta.MPAGRS)getMDB("MPAGRS", PAGRS);
      APBIL = (mvx.db.dta.FAPBIL)getMDB("FAPBIL", APBIL);
      PLINE = (mvx.db.dta.MPLINE)getMDB("MPLINE", PLINE);
   }

   // Entry parameters
   @ParameterList
   public cPXAPS450FncINmaintain pMaintain = null;
   @ParameterList
   public cPXAPS450FncINdelete pDelete = null;
   @ParameterList
   public cPXAPS450FncINcopy pCopy = null;
   @ParameterList
   public cPXAPS450FncINsettings pSettings = null;
   @ParameterList
   public cPXAPS450FncINchangeDivision pChangeDivision = null;
   @ParameterList
   public cPXAPS450FncINrejectInvoice pRejectInvoice = null;
   @ParameterList
   public cPXAPS450FncINapproveInvoice pApproveInvoice = null;
   @ParameterList
   public cPXAPS450FncINacknowledge pAcknowledge = null;
   @ParameterList
   public cPXAPS450FncOPlockForPrint pLockForPrint = null;
   @ParameterList
   public cPXAPS450FncOPlockForUpdate pLockForUpdate = null;
   @ParameterList
   public cPXAPS450FncOPlockForUpdAPL pLockForUpdAPL = null;
   @ParameterList
   public cPXAPS450FncOPlockForAdjLine pLockForAdjLine = null;
   @ParameterList
   public cPXAPS450FncOPunlock pUnlock = null;
   @ParameterList
   public cPXAPS450FncOPcheckIfWIP pCheckIfWIP = null;
   @ParameterList
   public cPXAPS450FncOPvalidateInv pValidateInv = null;
   @ParameterList
   public cPXAPS450FncOPreversePrintout pReversePrintout = null;
   @ParameterList
   public cPXAPS450FncOPresetToStatusNew pResetToStatusNew = null;

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINmaintain get_pAPS450Fnc_maintain() {
      if (pAPS450Fnc_maintain == null) {
         cPXAPS450FncINmaintain newPlist = new cPXAPS450FncINmaintain();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_maintain.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_maintain;
      }
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS451FncINmaintain get_pAPS451Fnc_maintain() {
      if (pAPS451Fnc_maintain == null) {
         cPXAPS451FncINmaintain newPlist = new cPXAPS451FncINmaintain();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS451Fnc_maintain.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS451Fnc_maintain;
      }
   }

   public void CRS428preCall() {// insert param into record for call
      pParameters.reset();
      pParameters.set(CRS428DS.getCRS428DS());
      pParameters.set(CRS428PDS.getCRS428PDS());
   }

   public void CRS428postCall() {// extract param from record after call
      pParameters.reset();
      pParameters.getString(CRS428DS.setCRS428DS());
      pParameters.getString(CRS428PDS.setCRS428PDS());
   }
   public MvxRecord pParameters = new MvxRecord();
   public sCRS428DS CRS428DS = new sCRS428DS(this);
   public sCRS428PDS CRS428PDS = new sCRS428PDS(this);
   //*STRUCDEF rKSTR{ 	
   public MvxStruct rKSTR = new MvxStruct(480); 	
   public MvxString KSTR = rKSTR.newString(0, 480); 	
   public MvxString KSTR01 = rKSTR.newString(0, 30); 	
   public MvxString KSTR02 = rKSTR.newString(30, 30); 	
   public MvxString KSTR03 = rKSTR.newString(60, 30); 	
   public MvxString KSTR04 = rKSTR.newString(90, 30); 	
   public MvxString KSTR05 = rKSTR.newString(120, 30); 	
   public MvxString KSTR06 = rKSTR.newString(150, 30); 	
   public MvxString KSTR07 = rKSTR.newString(180, 30); 	
   public MvxString KSTR08 = rKSTR.newString(210, 30); 	
   public MvxString KSTR09 = rKSTR.newString(240, 30); 	
   public MvxString KSTR10 = rKSTR.newString(270, 30); 	
   public MvxString KSTR11 = rKSTR.newString(300, 30); 	
   public MvxString KSTR12 = rKSTR.newString(330, 30); 	
   public MvxString KSTR13 = rKSTR.newString(360, 30); 	
   public MvxString KSTR14 = rKSTR.newString(390, 30); 	
   public MvxString KSTR15 = rKSTR.newString(420, 30); 	
   public MvxString KSTR16 = rKSTR.newString(450, 30); 	

   public MvxRecord FAPIBH_record = null;
   public MvxRecord saved_APIBR = null;
   public cPXCRS98X PXCRS98X = new cPXCRS98X(this);
   public cCRCommon CRCommon = new cCRCommon(this);
   public cCRCalendar CRCalendar = new cCRCalendar(this);
   public cPXRTVNBR PXRTVNBR = new cPXRTVNBR(this);
   public cPXRTVDDT PXRTVDDT = new cPXRTVDDT(this);
   public cPXRTVIMG PXRTVIMG = new cPXRTVIMG(this);
   public cPLRTVFNC PLRTVFNC = new cPLRTVFNC(this); 	 	
   public cPLCHKIF PLCHKIF = new cPLCHKIF(this);
   public cPLCHKFE PLCHKFE = new cPLCHKFE(this);
   public cPLCHKVO PLCHKVO = new cPLCHKVO(this);
   public cPLCHKAD PLCHKAD = new cPLCHKAD(this);
   public cPXCHKPER PXCHKPER = new cPXCHKPER(this);
   public cPXAPS450FncINmaintain pAPS450Fnc_maintain = null;
   public cPXAPS451FncINmaintain pAPS451Fnc_maintain = null;
   public sDSCUCD DSCUCD = new sDSCUCD(this);
   public sDSCSCD DSCSCD = new sDSCSCD(this);
   public sDSTEPY DSTEPY = new sDSTEPY(this);
   public sDSFFNC DSFFNC = new sDSFFNC(this);
   public sDSSDAP DSSDAP = new sDSSDAP(this);
   public sDSTECD DSTECD = new sDSTECD(this);
   public sDSPYME DSPYME = new sDSPYME(this);
   public sDSPYTP DSPYTP = new sDSPYTP(this);
   public sAPIDS APIDS = new sAPIDS(this);
   public sAPS905DS APS905DS = new sAPS905DS(this);
   public sCRS750DS CRS750DS = new sCRS750DS(this);
   public sCRMessageDS CRMessageDS = new sCRMessageDS(this);
   public boolean found_CSYSTR;
   public boolean found_CIDMAS;
   public boolean found_CIDVEN;
   public boolean found_CSUDIV;
   public boolean found_CMNDIV;
   public boolean found_CFACIL;
   public boolean found_MPHEAD;
   public boolean found_MITWHL;
   public boolean found_CSYTAB_CUCD;
   public boolean found_CSYTAB_TEPY;
   public boolean found_CSYTAB_PYME;
   public boolean found_CSYTAB_TDCD;
   public boolean found_CSYTAB_TECD;
   public boolean found_CSYTAB_SDAP;
   public boolean found_CSYTAB_FFNC;
   public boolean found_CSYTAB_REAP;
   public boolean found_CSYTAB_SERS;
   public boolean found_CSYTAB_CRTP;
   public boolean found_CSYTAB_CSCD;
   public boolean found_CSYTAB_SCRE;
   public boolean found_CSYTAB_PYTP;
   public boolean found_FCR040;
   public boolean found_FAPIBL;
   public boolean found_FAPIBA;
   public boolean found_FPLEDG;
   public boolean found_CIADDR;
   public boolean found_FAPRCD;
   public boolean found_CMNCMP;
   public boolean found_CBANAC;
   public boolean found_FGINPA;
   public boolean found_CSYPAR_APS905;
   public boolean found_CSYPAR_CRS750;
   public boolean found_MPAGRS;
   public boolean mailHeadWritten;
   public boolean found_CGEOJU;
   public int famFunction_CONO;
   public int XXAPCH; 	 	
   public int XXCDGN;
   public int nextTRNO;
   public long saved_INBN;
   public long XXMLID;
   public MvxRecord saved_APIBH = null;
   public MvxString XXVSER = cRefVSER.likeDef();
   public MvxString value_FEID = cRefFEID.likeDef();
   public MvxString value_FNCN = new MvxString(cRefFNCN.length() + 1);
   public MvxString value_FFNC = new MvxString(cRefFEID.length() + cRefFNCN.length() + 1);
   public MvxString value_STKY = new MvxString(10);
   public MvxString alpha2 = new MvxString(2);
   public MvxString alpha10 = new MvxString(10);
   public MvxString foundParam_CIDVEN = new MvxString(1);
   public MvxString foundParam_CSUDIV = new MvxString(1);
   public MvxString famFunction_DIVI = cRefDIVI.likeDef();
   public MvxString famFunction_SUNO = cRefSUNO.likeDef();
   public MvxString famFunction_SDAP = cRefSDAP.likeDef();
   public MvxString famFunction_IBTP = cRefIBTP.likeDef();
   public MvxString returnValueBKID = cRefBKID.likeDef();
   public MvxString tempSTAT = cRefSTAT.likeDef();
   public MvxString alreadyLockedByJob = new MvxString(1);
   public MvxString saved_DIVI = cRefDIVI.likeDef();
   public MvxString errorMessageData = new MvxString(100); // 100 pos can be used for error message
   public MvxString errorMSGID = cRefMSID.likeDef();
   public MvxString errorFieldName = cRefTX40.likeDef(); // 40 pos can be used for field name
   public MvxString saved_TECD = cRefTECD.likeDef();
   public cSRCOMCJB SRCOMCJB = new cSRCOMCJB(this);

   public MvxStruct rXXPAR1 = new MvxStruct(100);
   public MvxString XXPAR1 = rXXPAR1.newString(0, 100);
   public MvxString CSSPIC = rXXPAR1.newChar(0);
   public MvxString CSDSEQ = rXXPAR1.newString(cRefSPIC.length(), cRefPSEQ.length());
   public MvxString CSQTTP = rXXPAR1.newInt(cRefSPIC.length() + cRefPSEQ.length(), cRefQTTP.length());
   public MvxString CSINBN = rXXPAR1.newLong(cRefSPIC.length() + cRefPSEQ.length() + cRefQTTP.length(), cRefINBN.length());

   public MvxStruct rMessageData_X_00046 = new MvxStruct(15 + 15);
   public MvxString messageData_X_00046 = rMessageData_X_00046.newString(0, 15 + 15);
   public MvxString messageData_X_00046_BIST = rMessageData_X_00046.newLong(0, 15);
   public MvxString messageData_X_00046_SUPA = rMessageData_X_00046.newLong(15, 15);

   public MvxStruct rMessageData_X_00050 = new MvxStruct(40 + cRefOPT2.length() + cRefIBTP.length());
   public MvxString messageData_X_00050 = rMessageData_X_00050.newString(0, 40 + cRefOPT2.length() + cRefIBTP.length());
   public MvxString messageData_X_00050_TEXT = rMessageData_X_00050.newString(0, 40);
   public MvxString messageData_X_00050_OPT2 = rMessageData_X_00050.newString(40, cRefOPT2.length());
   public MvxString messageData_X_00050_IBTP = rMessageData_X_00050.newString(40 + cRefOPT2.length(), cRefIBTP.length());

   public MvxStruct rMessageData_X_00051 = new MvxStruct(40 + cRefOPT2.length() + cRefSUPA.length());
   public MvxString messageData_X_00051 = rMessageData_X_00051.newString(0, 40 + cRefOPT2.length() + cRefSUPA.length());
   public MvxString messageData_X_00051_TEXT = rMessageData_X_00051.newString(0, 40);
   public MvxString messageData_X_00051_OPT2 = rMessageData_X_00051.newString(40, cRefOPT2.length());
   public MvxString messageData_X_00051_SUPA = rMessageData_X_00051.newString(40 + cRefOPT2.length(), cRefSUPA.length());

   public MvxStruct rMessageData_AP_0028 = new MvxStruct(15 + cRefCHID.length());
   public MvxString messageData_AP_0028 = rMessageData_AP_0028.newString(0, 15 + cRefCHID.length());
   public MvxString messageData_AP_0028_INBN = rMessageData_AP_0028.newLong(0, 15);
   public MvxString messageData_AP_0028_CHID = rMessageData_AP_0028.newString(15, cRefCHID.length());

   public MvxStruct rMessageData_AP_0029 = new MvxStruct(15 + cRefCHID.length());
   public MvxString messageData_AP_0029 = rMessageData_AP_0029.newString(0, 15 + cRefCHID.length());
   public MvxString messageData_AP_0029_INBN = rMessageData_AP_0029.newLong(0, 15);
   public MvxString messageData_AP_0029_CHID = rMessageData_AP_0029.newString(15, cRefCHID.length());

   public MvxStruct rMessageData_AP10046 = new MvxStruct(cRefSUNO.length() + cRefPUNO.length());
   public MvxString messageData_AP10046 = rMessageData_AP10046.newString(0, cRefSUNO.length() + cRefPUNO.length());
   public MvxString messageData_AP10046_SUNO = rMessageData_AP10046.newString(0, cRefSUNO.length());
   public MvxString messageData_AP10046_PUNO = rMessageData_AP10046.newString(cRefSUNO.length(), cRefPUNO.length());

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

   public MvxStruct rXXVRNO = new MvxStruct(cRefVRNO.length());
   public MvxString XXVRNO = rXXVRNO.newString(0, cRefVRNO.length());
   public MvxString XXCSCD = rXXVRNO.newString(0, cRefCSCD.length() - 1);

   public FieldSelection BANACselection;
   public MvxString XFPGNM = cRefPGNM.likeDef();
   public MvxString BLANK = new MvxString(10);
   public int prevLatestRCDT;

   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(SYSTR);
      v.addElement(APIBH);
      v.addElement(APIBL);
      v.addElement(APIBR);
      v.addElement(APIBA);
      v.addElement(IDMAS);
      v.addElement(IDVEN);
      v.addElement(SUDIV);
      v.addElement(SYTAB);
      v.addElement(SYPAR);
      v.addElement(CR040);
      v.addElement(PLEDG);
      v.addElement(MNDIV);
      v.addElement(PFAMF);
      v.addElement(IADDR);
      v.addElement(APRCD);
      v.addElement(FACIL);
      v.addElement(PHEAD);
      v.addElement(PSUPR);
      v.addElement(ITWHL);
      v.addElement(MAILB);
      v.addElement(MAILP);
      v.addElement(MAILM);
      v.addElement(MNCMP);
      v.addElement(PPPAY);
      v.addElement(IDADR);
      v.addElement(BANAC);
      v.addElement(GEOJU);
      v.addElement(GINPA);
      v.addElement(PAGRS);
      v.addElement(XXVSER);
      v.addElement(value_FEID);
      v.addElement(value_FNCN);
      v.addElement(value_FFNC);
      v.addElement(value_STKY);
      v.addElement(alpha2);
      v.addElement(alpha10);
      v.addElement(foundParam_CIDVEN);
      v.addElement(foundParam_CSUDIV);
      v.addElement(PXCRS98X);
      v.addElement(CRCommon);
      v.addElement(CRCalendar);
      v.addElement(PXRTVNBR);
      v.addElement(PXRTVDDT);
      v.addElement(PXRTVIMG);
      v.addElement(PXCHKPER);
      v.addElement(pAPS450Fnc_maintain);
      v.addElement(pAPS451Fnc_maintain);
      v.addElement(PLRTVFNC);
      v.addElement(PLCHKIF);
      v.addElement(PLCHKFE);
      v.addElement(PLCHKVO);
      v.addElement(PLCHKAD);
      v.addElement(pMaintain);
      v.addElement(pDelete);
      v.addElement(pCopy);
      v.addElement(pSettings);
      v.addElement(pLockForPrint);
      v.addElement(pLockForUpdate);
      v.addElement(pLockForUpdAPL);
      v.addElement(pLockForAdjLine);
      v.addElement(pUnlock);
      v.addElement(pCheckIfWIP);
      v.addElement(pValidateInv);
      v.addElement(pApproveInvoice);
      v.addElement(pAcknowledge);
      v.addElement(pChangeDivision);
      v.addElement(pRejectInvoice);
      v.addElement(pReversePrintout);
      v.addElement(pResetToStatusNew);
      v.addElement(rXXPAR1);
      v.addElement(rMessageData_X_00046);
      v.addElement(rMessageData_X_00050);
      v.addElement(rMessageData_X_00051);
      v.addElement(rMessageData_AP_0028);
      v.addElement(rMessageData_AP_0029);
      v.addElement(rMessageData_AP10046);
      v.addElement(rMessageData_XDT0005);
      v.addElement(rMessageData_XFF0010);
      v.addElement(DSCUCD);
      v.addElement(DSCSCD);
      v.addElement(DSTEPY);
      v.addElement(DSFFNC);
      v.addElement(DSSDAP);
      v.addElement(DSTECD);
      v.addElement(DSPYME);
      v.addElement(DSPYTP);
      v.addElement(APS905DS);
      v.addElement(CRS750DS);
      v.addElement(rXXVRNO);
      v.addElement(famFunction_DIVI);
      v.addElement(famFunction_SUNO);
      v.addElement(famFunction_SDAP);
      v.addElement(famFunction_IBTP);
      v.addElement(returnValueBKID);
      v.addElement(tempSTAT);
      v.addElement(alreadyLockedByJob);
      v.addElement(saved_DIVI);
      v.addElement(errorFieldName);
      v.addElement(errorMSGID);
      v.addElement(saved_TECD);
      v.addElement(errorMessageData);
      v.addElement(APIDS);
      v.addElement(CRMessageDS);
      v.addElement(CRS428DS);
      v.addElement(CRS428PDS);
      v.addElement(rKSTR);
      v.addElement(SRCOMCJB);
      v.addElement(BANACselection);
      v.addElement(XFPGNM);
      v.addElement(BLANK);
      v.addElement(APBIL);
      v.addElement(PLINE);
      return version;
   }

   public void clearInstance() {
      super.clearInstance();
      found_CSYSTR = false;
      found_CIDMAS = false;
      found_CIDVEN = false;
      found_CSUDIV = false;
      found_CMNDIV = false;
      found_CFACIL = false;
      found_MPHEAD = false;
      found_MITWHL = false;
      found_CBANAC = false;
      found_FGINPA = false;
      found_CSYTAB_CUCD = false;
      found_CSYTAB_TEPY = false;
      found_CSYTAB_PYME = false;
      found_CSYTAB_TDCD = false;
      found_CSYTAB_TECD = false;
      found_CSYTAB_SDAP = false;
      found_CSYTAB_FFNC = false;
      found_CSYTAB_REAP = false;
      found_CSYTAB_SERS = false;
      found_CSYTAB_CRTP = false;
      found_CSYTAB_CSCD = false;
      found_CSYTAB_SCRE = false;
      found_CSYTAB_PYTP = false;
      found_CSYPAR_APS905 = false;
      found_CSYPAR_CRS750 = false;
      found_FCR040 = false;
      found_FAPIBL = false;
      found_FAPIBA = false;
      found_FPLEDG = false;
      found_CIADDR = false;
      found_FAPRCD = false;
      found_CMNCMP = false;
      found_MPAGRS = false;
      mailHeadWritten = false;
      found_CGEOJU = false;
      famFunction_CONO = 0;
      XXAPCH = 0;
      XXCDGN = 0;
      nextTRNO = 0;
      saved_INBN = 0L;
      XXMLID = 0L;
      prevLatestRCDT = 0;
   }

   public final static String PANELS_REQUIRED_FOR_ADD = "EF";

   public final static String ALLOWED_PANELS = PANELS_REQUIRED_FOR_ADD + "GT1234-";

   public final static String DEFAULT_PANELS = PANELS_REQUIRED_FOR_ADD + "GT";

   public final static String PYCL4_DOCUFO_FRVCOM = "FR-VCOM"; 

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

public final static String _GUID="4D33BB9E43C54fd3BC6EADF048F9113B";

public final static String _tempFixComment="";

public final static String _build="000000000000517";

public final static String _pgmName="APS450Fnc";

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
