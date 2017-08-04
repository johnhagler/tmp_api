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
*Nbr            Date   User id     Description*     JT-556359 140318 11893       Not possible to use Alias number in APS451
*     JT-595301 140516 16494       Use of different units of measure (qty and price) doesn't work in APS451
*     JT-604224 140606 16494       Use of different units of measure (qty and price) doesn't work in APS451
*Modification area - Business partner
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Customer
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*/

/**
*<BR><B><FONT SIZE=+2>Fnc: Manage Supplier invoice batch - lines</FONT></B><BR><BR>
*
* <PRE>
* Function program APS451Fnc is called by interactive program APS451 to
* mangage FAPIBL - Supplier invoice batch - Lines.
*
* The following interactions and operations are available:
*
* Interaction maintain
* --------------------
* parameter list: cPXAPS451FncINmaintain (instantiated as pMaintain )
* Interaction used when adding, changing or retrieving a invoice batch number.
* Returns any errors or notifications.
*
*
* Interaction delete
* ------------------
* parameter list: cPXAPS451FncINdelete (instantiated as pDelete )
* Interaction used when deleting a invoice batch number.
* Returns any errors or notifications.
*
*
* Interaction copy
* ----------------
* parameter list: cPXAPS451FncINcopy (instantiated as pCopy )
* Interaction used when copying a invoice batch number.
* Returns any errors or notifications.
*
*
* Interaction settings
* --------------------
* parameter list: cPXAPS451FncINsettings (instantiated as pSettings )
* Interaction used when setting parameters for APS451 function.
* Returns any errors or notifications.
*
* Returns notification 'XRE0103 - Record does not exist' in step INITIATE if there 
* is no settings record for the responsible. In that case default values are 
* returned for the parameters and a record will be written in step UDPATE.
*
* Interaction adjustLine
* ------------------------
* parameter list: cPXAPS451FncINadjustLine (instantiated as pAdjustLine )
* Interaction used to adjust the value of a line.
* Returns any errors or notifications.
*
* </PRE>
*/
public class APS451Fnc extends Function
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
      // Interaction Adjust Line
      // =======================
      } else if (pAdjustLine != null) {
         if (pAdjustLine.getStep() == cEnumStep.INITIATE) {
            do_adjustLine_initiate();
         } else if (pAdjustLine.getStep() == cEnumStep.VALIDATE) {
            do_adjustLine_validate();
         } else if (pAdjustLine.getStep() == cEnumStep.UPDATE) {
            do_adjustLine_update();
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
      boolean found_FAPIBL = false;
      // Get record
      // =========================================
      if (pMaintain.passFAPIBL && pMaintain.getMode() != cEnumMode.ADD) {
         // Move record from calling program
         moveFAPIBL(APIBL, pMaintain.APIBL);
         // Set primary keys
         pMaintain.DIVI.set().moveLeftPad(APIBL.getDIVI());
         pMaintain.INBN.set(APIBL.getINBN());
         pMaintain.TRNO.set(APIBL.getTRNO());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pMaintain.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         pMaintain.INBN.validateMANDATORYandConstraints();
         // Transaction number
         if (pMaintain.getMode() == cEnumMode.ADD) {
            // Transaction number is set automatically for ADD mode (just before writing the record)
            pMaintain.TRNO.clearValue();
            pMaintain.TRNO.setOUTAccess();
         } else {
            pMaintain.TRNO.setMANDATORYAccess();
         }
         pMaintain.TRNO.validateMANDATORYandConstraints();
         // Return error messages
         if (pMaintain.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pMaintain.parameters.setAllDISABLEDAccess();
            pMaintain.DIVI.setOUTAccess();
            pMaintain.INBN.setOUTAccess();
            pMaintain.TRNO.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBL.setCONO(currentCONO);
         APIBL.setDIVI().move(pMaintain.DIVI.get());
         APIBL.setINBN(pMaintain.INBN.get());
         APIBL.setTRNO(pMaintain.TRNO.get());
         found_FAPIBL = APIBL.CHAIN("00", APIBL.getKey("00"));
         if (pMaintain.getMode() == cEnumMode.ADD) {
            // - no check on record already exists since TRNO is retrieved at update.
            // Set default values
            APIBL.clearNOKEY("00");
            maintain_initiate_setDefaults();
         } else if (!found_FAPIBL) {
            // MSGID=WTR3103 Transaction number &1 does not exist
            pMaintain.messages.addError(this.DSPGM, "TRNO", "WTR3103", CRCommon.formatNumForMsg(APIBL.getTRNO()));
         }
         // Return error messages
         if (pMaintain.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pMaintain.parameters.setAllDISABLEDAccess();
            pMaintain.DIVI.setOUTAccess();
            pMaintain.INBN.setOUTAccess();
            pMaintain.TRNO.setOUTAccess();
            return;
         }
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pMaintain.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pMaintain.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, currentCONO, pMaintain.DIVI.get(), pMaintain.INBN.get());
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Invoice batch number &1 does not exist
         pMaintain.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
      }
      if (APIBH.getSUPA() > cRefSUPAext.VALIDATED_WITH_ERRORS() &&
          pMaintain.getMode() != cEnumMode.RETRIEVE) {
         if (pMaintain.getMode() == cEnumMode.ADD) {
            // XOP0102 = Create
            messageData_X_00051_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("XOP0102", "MVXCON")));
            messageData_X_00051_OPT2.moveLeftPad(" 1");
         } else {
            // XOP0200 = Change
            messageData_X_00051_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("XOP0200", "MVXCON")));
            messageData_X_00051_OPT2.moveLeftPad(" 2");
         }
         messageData_X_00051_SUPA.moveLeftPad(CRCommon.formatNumForMsg(APIBH.getSUPA()));
         // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
         pMaintain.messages.addError(this.DSPGM, "INBN", "X_00051", messageData_X_00051);
      }
      if (pMaintain.getMode() != cEnumMode.RETRIEVE) {
         // Check if Invoice batch number is Work in progress
         checkInvoiceForWIP(pMaintain.DIVI.get(), pMaintain.INBN.get(), pMaintain.messages);
      }
      if (pMaintain.messages.existError()) {
         // Set key parameters to access mode OUT and other parameters to DISABLED.
         pMaintain.parameters.setAllDISABLEDAccess();
         pMaintain.DIVI.setOUTAccess();
         pMaintain.INBN.setOUTAccess();
         pMaintain.TRNO.setOUTAccess();
         return;
      }
      // Validate optional/mandatory parameters in ADD mode
      // =========================================
      if (pMaintain.getMode() == cEnumMode.ADD) {
         // Indicate that default values should be retrieved for parameters related to main parameters
         pMaintain.work_getMainDefaults = true;
         // Line type, may be left blank in step INITIATE
         if (!pMaintain.RDTP.isBlank()) {
            maintain_validateRDTP();
            APIBL.setRDTP(pMaintain.RDTP.get());
         }
      }
      // Return error messages
      if (pMaintain.messages.existError()) {
         // Set key/optional/mandatory parameters to access mode OUT
         // and other parameters to DISABLED.
         pMaintain.parameters.setAllDISABLEDAccess();
         pMaintain.DIVI.setOUTAccess();
         pMaintain.INBN.setOUTAccess();
         pMaintain.TRNO.setOUTAccess();
         pMaintain.RDTP.setOUTAccess();
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
      APIBL.setIBLE(cRefIBLEext.NO_ERRORS());
   }

   /**
   * Sets parameters in step INITIATE of interaction maintain.
   */
   public void maintain_initiate_setParameters() {
      // Invoice batch type.
      pMaintain.IBTP.set().moveLeftPad(APIBH.getIBTP());
      // Supplier invoice number
      pMaintain.SINO.set().moveLeftPad(APIBH.getSINO());
      // Invoice status
      pMaintain.SUPA.set(APIBH.getSUPA());
      // Line type
      pMaintain.RDTP.set(APIBL.getRDTP());
      // Invoice line error
      pMaintain.IBLE.set(APIBL.getIBLE());
      // Message
      if (!APIBL.getMSID().isBlank()) {
         COMRTM(APIBL.getMSID().toString(), "MVXMSG", APIBL.getMSGD().toString());
         pMaintain.MSGD.set().moveLeftPad(SRCOMRCM.MSG);
      } else {
         pMaintain.MSGD.clearValue();
      }
      // Service code
      pMaintain.SERS.set(APIBL.getSERS());
      if (!pMaintain.quickMode()) {
         found_CSYTAB_SERS = cRefSERSext.getCSYTAB_SERS(SYTAB, found_CSYTAB_SERS, currentCONO, pMaintain.SERS.get());
         cRefSERSext.setAdditionalInfo(SYTAB, pMaintain.SERS.setAdditionalInfo(), pMaintain.SERS.setShortAddInfo());
      }
      // Net amount
      pMaintain.NLAM.set(APIBL.getNLAM());
      // Adjusted amount
      pMaintain.ADAB.set(APIBL.getADAB());
      // Currency
      pMaintain.CUCD.set().moveLeftPad(APIBH.getCUCD());
      maintain_setCurrencyDecimals();
      // Purchase order number
      pMaintain.PUNO.set().moveLeftPad(APIBL.getPUNO());
      // Purchase order line number
      pMaintain.PNLI.set(APIBL.getPNLI());
      // Purchase order line subnumber
      pMaintain.PNLS.set(APIBL.getPNLS());
      // Claim number
      pMaintain.CLAN.set().moveLeftPad(APIBL.getCLAN());
      // Claim line
      pMaintain.CLLN.set(APIBL.getCLLN());
      // VAT amount 1.
      pMaintain.VTA1.set(APIBL.getVTA1());
      // VAT amount 2.
      pMaintain.VTA2.set(APIBL.getVTA2());
      // Invoiced qty.
      pMaintain.IVQA.set(APIBL.getIVQA());
      // U/M (Invoiced qty).
      pMaintain.PUUN.set().moveLeftPad(APIBL.getPUUN());
      // VAT code.
      pMaintain.VTCD.set(APIBL.getVTCD());
      // Gross price.
      pMaintain.GRPR.set(APIBL.getGRPR());
      // U/M (Gross price).
      pMaintain.PPUN.set().moveLeftPad(APIBL.getPPUN());
      // Self billing agreement number.
      pMaintain.SBAN.set().moveLeftPad(APIBL.getSBAN());
      // Net price.
      pMaintain.NEPR.set(APIBL.getNEPR());
      // Sequence no.
      pMaintain.CDSE.set(APIBL.getCDSE());
      // Purchase order qty.
      pMaintain.PUCD.set(APIBL.getPUCD());
      // Costing element.
      pMaintain.CEID.set().moveLeftPad(APIBL.getCEID());
      // Charge text.
      pMaintain.CHGT.set().moveLeftPad(APIBL.getCHGT());
      // Gross amount.
      pMaintain.GLAM.set(APIBL.getGLAM());
      // Set dynamic caption for field GLAM (Gross amount / VAT rec amount)
      setDynamicCaptionGLAM();
      // Receiving number.
      pMaintain.REPN.set(APIBL.getREPN());
      // Discount.
      pMaintain.DIPC.set(APIBL.getDIPC());
      // Receipt type.
      pMaintain.RELP.set(APIBL.getRELP());
      // VAT rate 1.
      pMaintain.VTP1.set(APIBL.getVTP1());
      // VAT rate 2.
      pMaintain.VTP2.set(APIBL.getVTP2());
      // Discount amount.
      pMaintain.DIAM.set(APIBL.getDIAM());
      // Deliver note number.
      pMaintain.SUDO.set().moveLeftPad(APIBL.getSUDO());
      // Deliver note date.
      pMaintain.DNDT.set(APIBL.getDNDT());
      // Item number.
      pMaintain.ITNO.set().moveLeftPad(APIBL.getITNO());
      maintain_setItemDecimals();
      // Invoiced catch weight.
      pMaintain.IVCW.set(APIBL.getIVCW());
      // Alias number.
      pMaintain.POPN.set().moveLeftPad(APIBL.getPOPN());
      // Entry date
      pMaintain.RGDT.set(APIBL.getRGDT());
      // Change date
      pMaintain.LMDT.set(APIBL.getLMDT());
      // Change ID
      pMaintain.CHID.set().moveLeftPad(APIBL.getCHID());
      // Text ID
      pMaintain.TXID.set(APIBL.getTXID());
      // Change number
      pMaintain.work_CHNO = APIBL.getCHNO();
      pMaintain.work_FAPIBH_CHNO = APIBH.getCHNO();
      // ------------------------
      // Save parameter values - to be able to later check if values changes
      pMaintain.work_PUNO.moveLeftPad(pMaintain.PUNO.get());
      pMaintain.work_CLAN.moveLeftPad(pMaintain.CLAN.get());
      pMaintain.work_VTCD = pMaintain.VTCD.get();
      // Check if main parameters have been set
      if (!pMaintain.RDTP.isBlank()) {
         pMaintain.work_mainParamsSet = true;
      }
   }

   /**
   * Interaction maintain - step VALIDATE.
   */
   public void do_maintain_validate() {
      // Ensure notifications can be issued again (if the basic premises for the notifications has changed)
      if (pMaintain.PUNO.get().NE(pMaintain.work_PUNO)) {
         pMaintain.messages.forgetNotification("AP10037");
         pMaintain.messages.forgetNotification("AP10041");
         pMaintain.messages.forgetNotification("AP10046");
         pMaintain.messages.forgetNotification("AP10057");
      }
      if (pMaintain.CLAN.get().NE(pMaintain.work_CLAN)) {
         pMaintain.messages.forgetNotification("AP45102");
         pMaintain.messages.forgetNotification("AP45103");
      }
      // Perform validation
      maintain_validate();
      // Save parameter values - to be able to later check if values changes
      pMaintain.work_PUNO.moveLeftPad(pMaintain.PUNO.get());
      pMaintain.work_CLAN.moveLeftPad(pMaintain.CLAN.get());
      pMaintain.work_VTCD = pMaintain.VTCD.get();
   }

   /**
   * Interaction maintain - step VALIDATE.
   */
   public void maintain_validate() {
      // Invoice head
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, currentCONO, pMaintain.DIVI.get(), pMaintain.INBN.get());
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Invoice batch number &1 does not exist
         pMaintain.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
      }
      // Validate main parameters
      // =========================================
      // Line type
      maintain_validateRDTP();
      // Go no further if errors are found on main parameters
      if (pMaintain.messages.existError()) {
         return;
      }
      // Set dynamic caption for field GLAM (Gross amount / VAT rec amount)
      setDynamicCaptionGLAM();
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
         pMaintain.VTA1.set(mvxHalfAdjust(pMaintain.VTA1.get(), pMaintain.VTA1.getDecimals()));
         pMaintain.VTA2.set(mvxHalfAdjust(pMaintain.VTA2.get(), pMaintain.VTA2.getDecimals()));
         pMaintain.VTP1.set(mvxHalfAdjust(pMaintain.VTP1.get(), pMaintain.VTP1.getDecimals()));
         pMaintain.VTP2.set(mvxHalfAdjust(pMaintain.VTP2.get(), pMaintain.VTP2.getDecimals()));
         pMaintain.DIAM.set(mvxHalfAdjust(pMaintain.DIAM.get(), pMaintain.DIAM.getDecimals()));
         pMaintain.IVQA.set(mvxHalfAdjust(pMaintain.IVQA.get(), pMaintain.IVQA.getDecimals()));
         pMaintain.GRPR.set(mvxHalfAdjust(pMaintain.GRPR.get(), pMaintain.GRPR.getDecimals()));
         // No validation of other fields in ADD mode, since this is a batch entry program.
         return;
      }
      //  Service code
      pMaintain.SERS.validateMANDATORYandConstraints();
      found_CSYTAB_SERS = cRefSERSext.getCSYTAB_SERS(SYTAB, found_CSYTAB_SERS, currentCONO, pMaintain.SERS.get());
      cRefSERSext.setAdditionalInfo(SYTAB, pMaintain.SERS.setAdditionalInfo(), pMaintain.SERS.setShortAddInfo());
      pMaintain.SERS.validateExists(found_CSYTAB_SERS);
      // Net amount
      pMaintain.NLAM.validateMANDATORYandConstraints();
      // Purchase order number or Claim number must be entered
      if (!pMaintain.PUNO.isBlank() && !pMaintain.CLAN.isBlank()) {
         // MSGID=AP45101 PO number or Claim number must be entered
         pMaintain.messages.addError(this.DSPGM, "PUNO", "AP45101");   
      }      
      // Check if Purchase order number or Claim number is mandatory
      if (pMaintain.PUNO.isBlank() && pMaintain.CLAN.isBlank()) {
         if (((pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() ||
            pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) &&
            APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING())) {
            // MSGID=AP45101 PO number or Claim number must be entered
            pMaintain.messages.addError(this.DSPGM, "PUNO", "AP45101");
         }
      }          
      // Purchase order number
      pMaintain.PUNO.validateMANDATORYandConstraints();
      if (!pMaintain.PUNO.isBlank()) {
         if (pMaintain.PUNO.isAccessMANDATORYorOPTIONAL()) {
            found_MPHEAD = cRefPUNOext.getMPHEAD(PHEAD, found_MPHEAD, currentCONO, pMaintain.PUNO.get());
            pMaintain.PUNO.validateExists(found_MPHEAD);
            if (found_MPHEAD && PHEAD.getCUCD().NE(APIBH.getCUCD())) {
               // MSGID=AP10057 WARNING - The purchase order has a different currency (&1) than the invoice
               pMaintain.messages.addNotification(this.DSPGM, "PUNO", "AP10057", PHEAD.getCUCD());
            }
            if (found_MPHEAD && PHEAD.getPUSL().EQ("99")) {
               // MSGID=AP10046 WARNING - PO &2 has a different supplier (&1) than the invoice
               messageData_AP10046_SUNO.moveLeftPad(PHEAD.getSUNO());
               messageData_AP10046_PUNO.moveLeftPad(PHEAD.getPUNO());
               pMaintain.messages.addNotification(this.DSPGM, "PUNO", "AP10046", messageData_AP10046);
            }
            if (found_MPHEAD &&
               APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE()) && APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE_REQUEST()) &&
               PHEAD.getPUSL().EQ("85"))
            {
               // MSGID=AP10041 The purchase order has status &1
               pMaintain.messages.addNotification(this.DSPGM, "PUNO", "AP10041", PHEAD.getPUSL());
            }
            if (found_MPHEAD) {
               found_CFACIL = cRefFACIext.getCFACIL(FACIL, found_CFACIL, currentCONO, PHEAD.getFACI());
               if (found_CFACIL && FACIL.getDIVI().NE(pMaintain.DIVI.get())) {
                  // MSGID=AP10037 WARNING - The facility where the purchase order originated belongs to division &1.
                  pMaintain.messages.addNotification(this.DSPGM, "PUNO", "AP10037", FACIL.getFACI());
               }
            }
         }
      }
      // Purchase order line number
      if (!pMaintain.PUNO.isBlank() &&
         pMaintain.PNLI.isAccessMANDATORYorOPTIONAL()) {
         if (findMPLINERecord()) {
            if (APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE()) &&
               APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE_REQUEST()) &&
               PLINE.getPUSL().GE("85")) {
               // MSGID=WPU0901 Lowest status - purchase order &1 is invalid
               pMaintain.messages.addError(this.DSPGM, "PNLI", "WPU0901", PLINE.getPUSL());
            }
            if ((pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() ||
                 pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) &&
                APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING()) {
               if ((PLINE.getITNO().EQ(pMaintain.ITNO.get()) ||
                   (pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE() && 
                    pMaintain.ITNO.get().isBlank())) &&
                  // All purchase order line statuses valid for debit note
                   ((APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || 
                     APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) ||
                   PLINE.getPUSL().LT("85"))) {
                  if (pMaintain.PUUN.isAccessMANDATORYorOPTIONAL()) {
                     if (pMaintain.PUUN.get().isBlank()) {
                        pMaintain.PUUN.set().move(PLINE.getPUUN());
                        pMaintain.setNewEntryContext();
                     }
                  }
                  if (pMaintain.PPUN.isAccessMANDATORYorOPTIONAL()) {
                     if (pMaintain.PPUN.get().isBlank()) {
                        pMaintain.PPUN.set().move(PLINE.getPPUN());
                        pMaintain.setNewEntryContext();
                     }
                  }
                  if (pMaintain.PUCD.isAccessMANDATORYorOPTIONAL()) {
                     if (pMaintain.PUCD.get() == 0) {
                        if (PLINE.getPUCD() == 0) {
                           pMaintain.PUCD.set(1);
                        } else {
                           pMaintain.PUCD.set(PLINE.getPUCD());
                        }
                        pMaintain.setNewEntryContext();
                     }
                  }
               }
            }
            if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() &&
                pMaintain.ITNO.get().NE(PLINE.getITNO())) {
               // MSGID=AP05304 Item &1 on invoice does not match item on purchase order
               pMaintain.messages.addError(this.DSPGM, "ITNO", "AP05304", pMaintain.ITNO.get());
            }
         } else {
            // MSGID=WPN0103 Purchase order line &1 does not exist
            pMaintain.messages.addError(this.DSPGM, "PNLI", "WPN0103", CRCommon.formatNumForMsg(pMaintain.PNLI.get()));
         }
      }
      pMaintain.PNLI.validateMANDATORYandConstraints();
      // Purchase order line subnumber
      pMaintain.PNLS.validateMANDATORYandConstraints();
      // VAT amount 1
      pMaintain.VTA1.set(mvxHalfAdjust(pMaintain.VTA1.get(), pMaintain.VTA1.getDecimals()));
      pMaintain.VTA1.validateMANDATORYandConstraints();
      // VAT amount 2
      pMaintain.VTA2.set(mvxHalfAdjust(pMaintain.VTA2.get(), pMaintain.VTA2.getDecimals()));
      pMaintain.VTA2.validateMANDATORYandConstraints();
      // Item number.
      if (getItnoFromAlias()) {
         // Display the panel with new values if new ITNO found from alias
         pMaintain.setNewEntryContext();
      }
      pMaintain.ITNO.validateMANDATORYandConstraints();
      found_MITMAS = cRefITNOext.getMITMAS(ITMAS, found_MITMAS, currentCONO, pMaintain.ITNO.get());
      pMaintain.ITNO.validateExists(found_MITMAS);
      // U/M (Invoiced qty).
      pMaintain.PUUN.validateMANDATORYandConstraints();
      if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() ||
          pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) {
         found_CSYTAB_PUUN = cRefUNIText.getCSYTAB_UNIT(SYTAB, found_CSYTAB_PUUN, currentCONO, pMaintain.PUUN.get());
         pMaintain.PUUN.validateExists(found_CSYTAB_PUUN);
         if (found_MPLINE &&
            APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING() &&
            !pMaintain.PUUN.isBlank() &&
            pMaintain.PUUN.get().NE(PLINE.getPUUN())) {
            // MSGID=AP05306 Invoice U/M &1 not equal to purchase order U/M &2
            messageData_AP05306_PUUN_1.moveLeftPad(pMaintain.PUUN.get());
            messageData_AP05306_PUUN_2.moveLeftPad(PLINE.getPUUN());
            pMaintain.messages.addError(this.DSPGM, "PUUN", "AP05306", messageData_AP05306);
         }   
      }
      // Purchase price U/M
      pMaintain.PPUN.validateMANDATORYandConstraints();
      if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() ||
          pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) {
         if (!pMaintain.PPUN.isBlank()) {
            found_CSYTAB_PPUN = cRefUNIText.getCSYTAB_UNIT(SYTAB, found_CSYTAB_PPUN, currentCONO, pMaintain.PPUN.get());
            pMaintain.PPUN.validateExists(found_CSYTAB_PPUN);
         }
         if (found_MPLINE &&
            APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING() &&
            !pMaintain.PPUN.isBlank() &&
            pMaintain.PPUN.get().NE(PLINE.getPPUN())) {
            // MSGID=AP05306 Invoice U/M &1 not equal to purchase order U/M &2
            messageData_AP05306_PUUN_1.moveLeftPad(pMaintain.PPUN.get());
            messageData_AP05306_PUUN_2.moveLeftPad(PLINE.getPPUN());
            pMaintain.messages.addError(this.DSPGM, "PPUN", "AP05306", messageData_AP05306);
         }   
      }
      // Set decimals according to item
      maintain_setItemDecimals();
      // Invoiced qty.
      pMaintain.IVQA.set(mvxHalfAdjust(pMaintain.IVQA.get(), pMaintain.IVQA.getDecimals()));
      if (APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE()) && APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
         pMaintain.IVQA.validateMANDATORYandConstraints();
      }
      // VAT code.
      pMaintain.VTCD.validateMANDATORYandConstraints();
      if (!pMaintain.VTCD.isAccessDISABLED()) {
         if (!pMaintain.VTCD.isBlank()) {
            found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
            found_CSYTAB_VTCD = cRefVTCDext.getCSYTAB_VTCD(SYTAB, found_CSYTAB_VTCD, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), pMaintain.VTCD.get());
            pMaintain.VTCD.validateExists(found_CSYTAB_VTCD);
         }
         // Retrieve VAT rates if VAT code has been changed
         if ((pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() || pMaintain.RDTP.get() == cRefRDTPext.VAT()) &&
             pMaintain.VTCD.get() != pMaintain.work_VTCD) {
            if (found_CSYTAB_VTCD) {
               if (retrieveVatRate()) {
                  pMaintain.VTP1.set(VATPC.getVTP1());
                  pMaintain.VTP2.set(VATPC.getVTP2());
                  pMaintain.setNewEntryContext();
               }
            } else {
               pMaintain.VTP1.set(0d);
               pMaintain.VTP2.set(0d);
               pMaintain.setNewEntryContext();
            }
         }
      }
      // VAT rate 1
      pMaintain.VTP1.set(mvxHalfAdjust(pMaintain.VTP1.get(), pMaintain.VTP1.getDecimals()));
      pMaintain.VTP1.validateMANDATORYandConstraints();
      // VAT rate 2
      pMaintain.VTP2.set(mvxHalfAdjust(pMaintain.VTP2.get(), pMaintain.VTP2.getDecimals()));
      pMaintain.VTP2.validateMANDATORYandConstraints();
      // Gross price.
      pMaintain.GRPR.set(mvxHalfAdjust(pMaintain.GRPR.get(), pMaintain.GRPR.getDecimals()));
      if (APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE()) && APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
         pMaintain.GRPR.validateMANDATORYandConstraints();
      }
      // Self billing agreement number.
      pMaintain.SBAN.validateMANDATORYandConstraints();
      // Net price.
      pMaintain.NEPR.validateMANDATORYandConstraints();
      // Sequence no.
      pMaintain.CDSE.validateMANDATORYandConstraints();
      if (!pMaintain.CDSE.isBlank() &&
          pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE() &&
          APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING()) {
         found_MPOEXP = cRefCDSEext.getMPOEXP(POEXP, found_MPOEXP, currentCONO, pMaintain.PUNO.get(), pMaintain.PNLI.get(), pMaintain.PNLS.get(), pMaintain.CDSE.get());
         pMaintain.CDSE.validateExists(found_MPOEXP);
         if (!found_MPOEXP) {
            messageData_X_01011_CDSE.move(pMaintain.CDSE.get());
            messageData_X_01011_CEID.moveLeftPad(pMaintain.CEID.get());
            pMaintain.messages.addError(this.DSPGM, "CDSE", "X_01011", messageData_X_01011);
         }
      }
      // Purchase order qty.
      pMaintain.PUCD.validateMANDATORYandConstraints();
      // Costing element.
      pMaintain.CEID.validateMANDATORYandConstraints();
      if (pMaintain.RDTP.get() == cRefRDTPext.ORDER_CHARGES() ||
          pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) {
          found_MPCELE = cRefCEIDext.getMPCELE(PCELE, found_MPCELE, currentCONO, pMaintain.CEID.get());
          pMaintain.CEID.validateExists(found_MPCELE);
          if (found_MPCELE && 
              PCELE.getWSOP().EQ("90")) {             
             pMaintain.messages.addError(this.DSPGM, "CEID", "AP45105", pMaintain.CEID.get());             
          }
      }
      // Gross amount.
      pMaintain.GLAM.validateMANDATORYandConstraints();
      // Receiving number.
      pMaintain.REPN.validateMANDATORYandConstraints();
      // Discount.
      pMaintain.DIPC.set(mvxHalfAdjust(pMaintain.DIPC.get(), pMaintain.DIPC.getDecimals()));
      pMaintain.DIPC.validateMANDATORYandConstraints();
      // Receipt type.
      pMaintain.RELP.validateMANDATORYandConstraints();
      // Discount amount.
      pMaintain.DIAM.set(mvxHalfAdjust(pMaintain.DIAM.get(), pMaintain.DIAM.getDecimals()));
      pMaintain.DIAM.validateMANDATORYandConstraints();
      // Deliver note number.
      pMaintain.SUDO.validateMANDATORYandConstraints();
      // Deliver note date.
      pMaintain.DNDT.validateMANDATORYandConstraints();
      if (!pMaintain.DNDT.isBlank()) {
         if (!CRCalendar.lookUpDate(currentCONO, pMaintain.DIVI.get(), pMaintain.DNDT.get())) {
            // MSGID=WDN2001 Delivery note date &1 is invalid
            pMaintain.messages.addError(this.DSPGM, "DNDT", "WDN2001", 
               CRCommon.formatDateForMsg(pMaintain.DNDT.get(), pMaintain.getDTFM(), pMaintain.getDSEP()));
         }
      }
      // Invoiced catch weight.
      pMaintain.IVCW.validateMANDATORYandConstraints();
      // Alias number.
      pMaintain.POPN.validateMANDATORYandConstraints();     
      // Claim number
      pMaintain.CLAN.validateMANDATORYandConstraints();
      if (!pMaintain.CLAN.isBlank()) {
         found_MPCLAH = cRefCLANext.getMPCLAH(PCLAH, found_MPCLAH, currentCONO, pMaintain.CLAN.get());
         if (pMaintain.CLAN.isAccessMANDATORYorOPTIONAL()) {
            if (!found_MPCLAH) {
               // MSGID=WCLA603 Claim number &1 does not exist
               pMaintain.messages.addError(this.DSPGM, "CLAN", "WCLA603", pMaintain.CLAN.get());
            }
            found_CFACIL = cRefFACIext.getCFACIL(FACIL, found_CFACIL, currentCONO, PCLAH.getFACI());
            if (found_CFACIL && FACIL.getDIVI().NE(pMaintain.DIVI.get())) {
               // MSGID=AP45102 WARNING - The facility where the claim order originated belongs to division &1.
               pMaintain.messages.addNotification(this.DSPGM, "CLAN", "AP45102", FACIL.getFACI());
            }
            if (PCLAH.getSUNO().NE(APIBH.getSUNO())) {
               // MSGID=AP45103 WARNING - Claim &2 has a different supplier (&1) than the invoice
               messageData_AP45103_SUNO.moveLeftPad(PCLAH.getSUNO());
               messageData_AP45103_CLAN.moveLeftPad(PCLAH.getCLAN());
               pMaintain.messages.addNotification(this.DSPGM, "CLAN", "AP45103", messageData_AP45103);
            }   
         }
      }
      // Claim line number
      pMaintain.CLLN.validateMANDATORYandConstraints();
      if (!pMaintain.CLAN.isBlank() &&
         !pMaintain.CLLN.isBlank() &&  
         pMaintain.CLLN.isAccessMANDATORYorOPTIONAL()) {
         found_MPCLAL = cRefCLLNext.getMPCLAL(PCLAL, found_MPCLAL, currentCONO, pMaintain.CLAN.get(), pMaintain.CLLN.get());
         pMaintain.CLLN.validateExists(found_MPCLAL);
         if (pMaintain.ITNO.get().NE(PCLAL.getITNO())) {
            // MSGID=AP45104 Item &1 on invoice does not match item on claim order
            pMaintain.messages.addError(this.DSPGM, "ITNO", "AP45104", pMaintain.ITNO.get());
         }
      }     
   }

   /**
   * Validate line type.
   * Messages are returned in pMaintain.messages.
   */
   public void maintain_validateRDTP() {
      pMaintain.RDTP.validateMANDATORYandConstraints();
   }

   /**
   * Sets default values for parameters in step VALIDATE.
   */
   public void maintain_validate_setDefaults() {
      // Set default values
      // -------------------------------
      // Purchase order line number
      if (pMaintain.PNLI.get() == 0) {
         // Set line number for current Item or zero
         if (maintain_getLineNoFromMPLINE()) {
            // Display the panel with new values
            pMaintain.setNewEntryContext();
         }
      }
      // Item number. Get item number from alias (POPN) if ITNO is blank
      if (getItnoFromAlias()) {
         // Display the panel with new values if new ITNO found from alias
         pMaintain.setNewEntryContext();
      }
      // VAT code.
      if (!pMaintain.VTCD.isAccessDISABLED()) {
         if (isVatCalculated(pMaintain.DIVI.get())) {
            // Try to find VAT code if not set before
            // VAT line
            if (pMaintain.RDTP.get() == cRefRDTPext.VAT() &&
                pMaintain.VTCD.isBlank()) 
            {
               // Try to find VAT code
               if (!pMaintain.VTP1.isBlank() || !pMaintain.VTP2.isBlank()) {
                  // Try to find VAT code via CVATPC
                  if (retrieveVATCode()) {
                     pMaintain.VTCD.set(VATPC.getVTCD());
                     pMaintain.setNewEntryContext();
                  }
               }
               if (pMaintain.VTP1.isBlank() && pMaintain.VTP2.isBlank()) {
                  // Take VAT code from CIDVEN
                  foundParam_CIDVEN.moveLeft(toChar(found_CIDVEN));
                  foundParam_CSUDIV.moveLeft(toChar(found_CSUDIV));
                  if (APIBH.getSPYN().isBlank()) {
                     cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, currentCONO, pMaintain.DIVI.get(),  APIBH.getSUNO());
                  } else {
                     cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, currentCONO, pMaintain.DIVI.get(),  APIBH.getSPYN());
                  }
                  found_CIDVEN = foundParam_CIDVEN.getBoolean();
                  found_CSUDIV = foundParam_CSUDIV.getBoolean();
                  pMaintain.VTCD.set(IDVEN.getVTCD());
                  pMaintain.setNewEntryContext();
               }
            }
            // Item line
            if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() &&
                pMaintain.VTCD.isBlank()  &&
                APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING()) 
            {
               // Try to find VAT code from MPLINE for item line when invoice matching is active
               if (cRefPNLIext.getMPLINE(PLINE, false, currentCONO, pMaintain.PUNO.get(), pMaintain.PNLI.get(), pMaintain.PNLS.get())) {
                  if (PLINE.getVTCD() != 0) {
                     pMaintain.VTCD.set(PLINE.getVTCD());
                     pMaintain.setNewEntryContext();
                  }
               }
            }
         }
      }
      // Costing element.
      if (!pMaintain.CEID.isAccessDISABLED()) {
         if (pMaintain.RDTP.get() == cRefRDTPext.ORDER_CHARGES() ||
             pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) {
            if (pMaintain.CEID.isBlank()) {
               // Retrieve Costing element based on the given description
               if (retrieveCostingElement()) {
                  pMaintain.setNewEntryContext();
               }
            }
         }
      }
      // -
      // Don't set defaults again
      // -------------------------------
      // Defaults for main parameters
      pMaintain.work_getMainDefaults = false;
   }

   /**
   * Sets decimals for amounts in the parameter list based on the currency code.
   */
   public void maintain_setCurrencyDecimals() {
      found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, pMaintain.CUCD.get());
      if (found_CSYTAB_CUCD) {
         cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
         pMaintain.NLAM.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.VTA1.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.VTA2.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.GLAM.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.DIAM.setDecimals(DSCUCD.getYQDCCD());
         pMaintain.ADAB.setDecimals(DSCUCD.getYQDCCD());
      }
   }

   /**
   * Sets decimals for some fields based on item data (ITNO, PUUN, PPUN).
   */
   public void maintain_setItemDecimals() {
      // Read alternative units and override no of decimals
      if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() ||
          pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) 
      {
         found_MITMAS = cRefITNOext.getMITMAS(ITMAS, found_MITMAS, currentCONO, pMaintain.ITNO.get());
         if (found_MITMAS) {
            pMaintain.IVQA.setDecimals(ITMAS.getDCCD());
            found_MITAUN = cRefPUUNext.getMITAUN(ITAUN, found_MITAUN, currentCONO, pMaintain.ITNO.get(), pMaintain.PUUN.get());
            if (found_MITAUN) {
               pMaintain.IVQA.setDecimals(ITAUN.getDCCD());
            }
            if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE()) {
               pMaintain.GRPR.setDecimals(ITMAS.getPDCC());
               pMaintain.NEPR.setDecimals(ITMAS.getPDCC());
            }
            if (ITMAS.getACTI() >= 2) {
               found_MITAUN = cRefPPUNext.getMITAUN(ITAUN, found_MITAUN, currentCONO, pMaintain.ITNO.get(), pMaintain.PPUN.get());
               if (found_MITAUN) {
                  if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE()) {
                     pMaintain.GRPR.setDecimals(ITAUN.getDCCD());
                     pMaintain.NEPR.setDecimals(ITAUN.getDCCD());
                  }
               }
            }
         }
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
   }

   /**
   * Perform explicit transaction APS451FncINmaintain
   */
   @Transaction(name=cPXAPS451FncINmaintain.LOGICAL_NAME, primaryTable="FAPIBL") 
   public void transaction_APS451FncINmaintain() {
      // Declaration
      boolean found_FAPIBL = false;
      boolean alreadyLocked = false;
      // Set Invoice batch number to status - Update in progress
      if (!lockInvoiceForUpdate(pMaintain.DIVI.get(), pMaintain.INBN.get(), pMaintain.messages, alreadyLockedByJob, pMaintain.work_FAPIBH_CHNO)) {
         return;
      }
      alreadyLocked = alreadyLockedByJob.getBoolean();
      // Check if update is possible
      // =========================================
      APIBL.setCONO(currentCONO);
      APIBL.setDIVI().moveLeftPad(pMaintain.DIVI.get());
      APIBL.setINBN(pMaintain.INBN.get());
      if (pMaintain.getMode() == cEnumMode.ADD) {
         FAPIBL_record.setRecord(APIBL);  // Save record
         // Fetch next transaction number
         APIBL.SETGT("00", APIBL.getKey("00", 3));
         if (!APIBL.REDPE("00", APIBL.getKey("00", 3))) {
            nextTRNO = 1;
         } else {
            nextTRNO = APIBL.getTRNO() + 1;
         }
         pMaintain.TRNO.set(nextTRNO);
         APIBL.setRecord(FAPIBL_record); // Restore record
      }
      APIBL.setTRNO(pMaintain.TRNO.get());
      found_FAPIBL = APIBL.CHAIN_LOCK("00", APIBL.getKey("00"));
      // Update of deleted record
      if (pMaintain.getMode() == cEnumMode.CHANGE && !found_FAPIBL) {
         // MSGID=XDE0001 The record has been deleted by another user
         pMaintain.messages.addError(this.DSPGM, "", "XDE0001");
         if (!alreadyLocked) {
            // Set Invoice batch number to status - Unlocked
            unlockInvoice(pMaintain.DIVI.get(), pMaintain.INBN.get());
         }
         return;
      }
      // Update of changed record
      if (pMaintain.getMode() == cEnumMode.CHANGE && found_FAPIBL && pMaintain.work_CHNO != APIBL.getCHNO()) {
         APIBL.UNLOCK("00");
         // MSGID=XUP0001 The record has been changed by user &1
         pMaintain.messages.addError(this.DSPGM, "", "XUP0001", APIBL.getCHID());
         if (!alreadyLocked) {
            // Set Invoice batch number to status - Unlocked
            unlockInvoice(pMaintain.DIVI.get(), pMaintain.INBN.get());
         }
         return;
      }
      // Add to an existing record
      if (pMaintain.getMode() == cEnumMode.ADD && found_FAPIBL) {
         APIBL.UNLOCK("00");
         // MSGID=XAD0001 A record has been entered by user &1
         pMaintain.messages.addError(this.DSPGM, "", "XAD0001", APIBL.getCHID());
         if (!alreadyLocked) {
            // Set Invoice batch number to status - Unlocked
            unlockInvoice(pMaintain.DIVI.get(), pMaintain.INBN.get());
         }
         return;
      }
      // Move parameters to DB-record
      // =========================================
      maintain_update_setValues();
      // Perform update
      // =========================================
      if (!maintain_update_perform(found_FAPIBL)) {
         if (!alreadyLocked) {
            // Set Invoice batch number to status - Unlocked
            unlockInvoice(pMaintain.DIVI.get(), pMaintain.INBN.get());
         }
         return;
      }
      if (!alreadyLocked) {
         // Set Invoice batch number to status - Unlocked
         unlockInvoice(pMaintain.DIVI.get(), pMaintain.INBN.get());
      }
   }

   /**
   * Sets database field values in step UPDATE of interaction maintain.
   * Moves values from the parameter list to the database fields.
   */
   public void maintain_update_setValues() {
      double IVQAinBasicUM = 0d;
      double GRPRinBasicUM = 0d;
      // Save record
      FAPIBL_record.setRecord(APIBL);
      // Set fields
      // Get head record
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, currentCONO, APIBL.getDIVI(), APIBL.getINBN());
      if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || 
          APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST()) ||
          LDAZZ.FPNM.EQ("PPCRTSBT")) 
      {
         // Debit note should have NLAM and GLAM already set
         // When creating self billing transactions, NLAM and GLAM is set by PPCRTSBT
      } else {
         // Check purchase price quantity
         // Must be entered line type = 1 and invoice matching
         if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() ||
             pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) {
            IVQAinBasicUM = pMaintain.IVQA.get();
            GRPRinBasicUM = pMaintain.GRPR.get();
            found_MITMAS = cRefITNOext.getMITMAS(ITMAS, found_MITMAS, currentCONO, pMaintain.ITNO.get());
            //   Qty - If Alternative U/M is given, convert to Basic U/M
            if (pMaintain.PUUN.get().NE(ITMAS.getUNMS())) {
               ITAUN.setCONO(currentCONO);
               ITAUN.setITNO().move(ITMAS.getITNO()); 
               ITAUN.setAUTP(1); 
               ITAUN.setALUN().move(pMaintain.PUUN.get()); 
               if (ITAUN.CHAIN("00", ITAUN.getKey("00"))) { 
                  if (!isBlank(ITAUN.getCOFA(), 9)) {
                     if (ITAUN.getDMCF() == 1) {
                        IVQAinBasicUM *= ITAUN.getCOFA(); 
                     } else { 
                        IVQAinBasicUM /= ITAUN.getCOFA(); 
                     }
                  }
               }
            }  
            //   Price - If Alternative U/M is given, convert to Basic U/M
            if (pMaintain.PPUN.get().NE(ITMAS.getUNMS())) {
               ITAUN.setCONO(currentCONO);
               ITAUN.setITNO().move(ITMAS.getITNO()); 
               ITAUN.setAUTP(2); 
               ITAUN.setALUN().move(pMaintain.PPUN.get()); 
               if (ITAUN.CHAIN("00", ITAUN.getKey("00"))) { 
                  if (!isBlank(ITAUN.getCOFA(), 9)) {
                     if (ITAUN.getDMCF() == 2) {
                        GRPRinBasicUM *= ITAUN.getCOFA(); 
                     } else { 
                        GRPRinBasicUM /= ITAUN.getCOFA();     
                     }
                  }
               }
            }
            if (!pMaintain.IVQA.isBlank()) {
               if (pMaintain.PUCD.isBlank()) {
                  pMaintain.GLAM.set(GRPRinBasicUM * IVQAinBasicUM);
               } else {
                  pMaintain.GLAM.set((GRPRinBasicUM * IVQAinBasicUM) / pMaintain.PUCD.get());
               }
               pMaintain.NEPR.set(GRPRinBasicUM);
               if (!isBlank(pMaintain.DIPC.get())) {
                  pMaintain.NEPR.set(GRPRinBasicUM - (GRPRinBasicUM * pMaintain.DIPC.get() / 100));
               }
               if (pMaintain.PUCD.isBlank()) {
                  pMaintain.NLAM.set(pMaintain.NEPR.get() * IVQAinBasicUM);
               } else {
                  pMaintain.NLAM.set((pMaintain.NEPR.get() * IVQAinBasicUM) / pMaintain.PUCD.get());
               }
            } else {
               if (pMaintain.PUCD.isBlank()) {
                  pMaintain.GLAM.set(GRPRinBasicUM);
               } else {
                  pMaintain.GLAM.set(GRPRinBasicUM/pMaintain.PUCD.get());
               }
               pMaintain.NEPR.set(GRPRinBasicUM);
               if (!isBlank(pMaintain.DIPC.get())) {
                  pMaintain.NEPR.set(GRPRinBasicUM - (GRPRinBasicUM * pMaintain.DIPC.get() / 100));
               }
               if (pMaintain.PUCD.isBlank()) {
                  pMaintain.NLAM.set(pMaintain.NEPR.get());
               } else {
                  pMaintain.NLAM.set(pMaintain.NEPR.get()/pMaintain.PUCD.get());
               }
            }
         }
      }
      APIBL.setRDTP(pMaintain.RDTP.get());
      APIBL.setITNO().move(pMaintain.ITNO.get());
      APIBL.setCDSE(pMaintain.CDSE.get());
      APIBL.setCEID().move(pMaintain.CEID.get());
      APIBL.setCHGT().move(pMaintain.CHGT.get());
      APIBL.setIBLE(pMaintain.IBLE.get());
      APIBL.setIVQA(pMaintain.IVQA.get());
      APIBL.setPUUN().move(pMaintain.PUUN.get());
      // Set NLAM
      double oldNLAM = APIBL.getNLAM(); 
      APIBL.setNLAM(pMaintain.NLAM.get());
      if (pMaintain.RDTP.get() == cRefRDTPext.VAT()) {
         APIBL.setNLAM(pMaintain.VTA1.get() + pMaintain.VTA2.get());
      } else {
         if (!isBlank(pMaintain.DIAM.get())) {
            APIBL.setNLAM(pMaintain.GLAM.get() - pMaintain.DIAM.get());
         }
      }
      APIBL.setNLAM(mvxHalfAdjust(APIBL.getNLAM(), pMaintain.NLAM.getDecimals()));
      pMaintain.NLAM.set(APIBL.getNLAM());
      double NLAMchange = APIBL.getNLAM() - oldNLAM;
      // ---
      // Set GLAM
      double oldGLAM = APIBL.getGLAM(); 
      APIBL.setGLAM(mvxHalfAdjust(pMaintain.GLAM.get(), pMaintain.GLAM.getDecimals()));
      pMaintain.GLAM.set(APIBL.getGLAM());
      double GLAMchange = APIBL.getGLAM() - oldGLAM;
      // ---
      APIBL.setGRPR(pMaintain.GRPR.get());
      APIBL.setNEPR(mvxHalfAdjust(pMaintain.NEPR.get(), pMaintain.NEPR.getDecimals()));
      pMaintain.NEPR.set(APIBL.getNEPR());
      APIBL.setPPUN().move(pMaintain.PPUN.get());
      APIBL.setPUCD(pMaintain.PUCD.get());
      APIBL.setIVCW(pMaintain.IVCW.get());
      APIBL.setSERS(pMaintain.SERS.get());
      APIBL.setVTCD(pMaintain.VTCD.get());
      // Adjusted amount
      if (LDAZZ.FPNM.EQ("PPS124")) { // Supplier claim invoice - create
         if (pMaintain.RDTP.get() == cRefRDTPext.CLAIM_LINE()) {
            pMaintain.ADAB.set(APIBL.getNLAM());
         }
      }
      APIBL.setADAB(pMaintain.ADAB.get());
      // Set VTA1, VTA2
      double oldVTAM = APIBL.getVTA1() + APIBL.getVTA2();
      APIBL.setVTA1(pMaintain.VTA1.get());
      APIBL.setVTA2(pMaintain.VTA2.get());
      double newVTAM = APIBL.getVTA1() + APIBL.getVTA2();
      double VTAMchange = newVTAM - oldVTAM;
      // ---
      APIBL.setVTP1(pMaintain.VTP1.get());
      APIBL.setVTP2(pMaintain.VTP2.get());
      APIBL.setDIAM(pMaintain.DIAM.get());
      APIBL.setDIPC(pMaintain.DIPC.get());
      APIBL.setPUNO().move(pMaintain.PUNO.get());
      APIBL.setPNLI(pMaintain.PNLI.get());
      APIBL.setPNLS(pMaintain.PNLS.get());
      APIBL.setSUDO().move(pMaintain.SUDO.get());
      APIBL.setDNDT(pMaintain.DNDT.get());
      APIBL.setREPN(pMaintain.REPN.get());
      APIBL.setRELP(pMaintain.RELP.get());
      APIBL.setSBAN().move(pMaintain.SBAN.get());
      APIBL.setPOPN().move(pMaintain.POPN.get());
      APIBL.setCLAN().move(pMaintain.CLAN.get());
      APIBL.setCLLN(pMaintain.CLLN.get());
      // Update amounts on invoice head
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().moveLeftPad(APIBL.getDIVI());
      APIBH.setINBN(APIBL.getINBN());
      found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      if (found_FAPIBH) {
         if (LDAZZ.FPNM.NE("PPS124") &&
             LDAZZ.FPNM.NE("APMNGI07") &&
             LDAZZ.FPNM.NE("PPCRTSBT")) {
            // VAT amount
            APIBH.setVTAM(APIBH.getVTAM() + VTAMchange);
            APIBH.setVTAM(mvxHalfAdjust(APIBH.getVTAM(), pMaintain.VTA1.getDecimals()));
            // Total line amount
            if (pMaintain.RDTP.get() != cRefRDTPext.VAT()) {
               APIBH.setTLNA(APIBH.getTLNA() + NLAMchange);
               APIBH.setTLNA(mvxHalfAdjust(APIBH.getTLNA(), pMaintain.NLAM.getDecimals()));
            }
         } else if (LDAZZ.FPNM.EQ("PPCRTSBT")) {  // Self billing invoice - create
            if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() ||
                pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE() ||
                pMaintain.RDTP.get() == cRefRDTPext.ORDER_CHARGES())
            {
               // Tot line amount
               APIBH.setTLNA(APIBH.getTLNA() + NLAMchange);
               APIBH.setTLNA(mvxHalfAdjust(APIBH.getTLNA(), pMaintain.NLAM.getDecimals()));
               // Foreign currency amount
               APIBH.setCUAM(APIBH.getCUAM() + NLAMchange);
               APIBH.setCUAM(mvxHalfAdjust(APIBH.getCUAM(), pMaintain.NLAM.getDecimals()));
               // Cash discount base
               APIBH.setTASD(APIBH.getTLNA());
            }
            if (pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE() ||
                pMaintain.RDTP.get() == cRefRDTPext.ORDER_CHARGES()) 
            {
               // Tot charge amount
               APIBH.setTCHG(APIBH.getTCHG() + NLAMchange);
               APIBH.setTCHG(mvxHalfAdjust(APIBH.getTCHG(), pMaintain.NLAM.getDecimals()));
            }
            if (pMaintain.RDTP.get() == cRefRDTPext.VAT()) {
               // VAT amount
               APIBH.setVTAM(APIBH.getVTAM() + VTAMchange);
               APIBH.setVTAM(mvxHalfAdjust(APIBH.getVTAM(), pMaintain.VTA1.getDecimals()));
               // Foreign currency amount
               APIBH.setCUAM(APIBH.getCUAM() + VTAMchange);
               APIBH.setCUAM(mvxHalfAdjust(APIBH.getCUAM(), pMaintain.NLAM.getDecimals()));
               // Total taxable amount
               APIBH.setTTXA(APIBH.getVTAM());
            }
         } else if (LDAZZ.FPNM.EQ("PPS124")) {  // Supplier claim invoice - create
            if (pMaintain.RDTP.get() == cRefRDTPext.CLAIM_LINE()) {
               // Tot line amount
               APIBH.setTLNA(APIBH.getTLNA() + NLAMchange);
               APIBH.setTLNA(mvxHalfAdjust(APIBH.getTLNA(), pMaintain.NLAM.getDecimals()));
               // Foreign currency amount
               APIBH.setCUAM(APIBH.getCUAM() + NLAMchange);
               APIBH.setCUAM(mvxHalfAdjust(APIBH.getCUAM(), pMaintain.NLAM.getDecimals()));
               // Adjusted amount
               APIBH.setADAB(APIBH.getCUAM());
               // Cash discount base
               APIBH.setTASD(APIBH.getTLNA());
               // Total taxable amount
               APIBH.setTTXA(APIBH.getTLNA());
            }
            if (pMaintain.RDTP.get() == cRefRDTPext.VAT()) {
               // VAT amount
               APIBH.setVTAM(APIBH.getVTAM() + VTAMchange);
               APIBH.setVTAM(mvxHalfAdjust(APIBH.getVTAM(), pMaintain.VTA1.getDecimals()));
               // Foreign currency amount
               APIBH.setCUAM(APIBH.getCUAM() + VTAMchange);
               APIBH.setCUAM(mvxHalfAdjust(APIBH.getCUAM(), pMaintain.NLAM.getDecimals()));
               // Adjusted amount
               APIBH.setADAB(APIBH.getCUAM());
            }
         } else if (LDAZZ.FPNM.EQ("APMNGI07")) {  // Supplier Debit note invoice - create
            if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() ||
                pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) {
               // Foreign currency amount
               double CUAMchange = 0d;
               if (!equals(GLAMchange, pMaintain.GLAM.getDecimals(), 0d)) {
                  CUAMchange = GLAMchange;
               } else {
                  CUAMchange = NLAMchange;
               }
               APIBH.setCUAM(APIBH.getCUAM() + CUAMchange);
               APIBH.setCUAM(mvxHalfAdjust(APIBH.getCUAM(), pMaintain.NLAM.getDecimals()));
               // Tot line amount
               APIBH.setTLNA(APIBH.getTLNA() + CUAMchange);
               APIBH.setTLNA(mvxHalfAdjust(APIBH.getTLNA(), pMaintain.NLAM.getDecimals()));
               // Cash discount base
               APIBH.setTASD(APIBH.getCUAM());
               // Total due amount
               APIBH.setTOPA(APIBH.getCUAM());
            }
            if (pMaintain.RDTP.get() == cRefRDTPext.VAT()) {
               // VAT amount
               APIBH.setVTAM(APIBH.getVTAM() + VTAMchange);
               APIBH.setVTAM(mvxHalfAdjust(APIBH.getVTAM(), pMaintain.VTA1.getDecimals()));
               // Foreign currency amount
               APIBH.setCUAM(APIBH.getTLNA() + VTAMchange);
               APIBH.setCUAM(mvxHalfAdjust(APIBH.getCUAM(), pMaintain.NLAM.getDecimals()));
               // Total taxable amount
               APIBH.setTTXA(APIBH.getVTAM());
               // Cash discount base
               APIBH.setTASD(APIBH.getCUAM());
               // Total due amount
               APIBH.setTOPA(APIBH.getCUAM());
            }
         }
         FAPIBH_setChanged();
         APIBH.UPDAT("00");
      }
   }

   /**
   * Effectuate update in interaction maintain.
   * @param found_FAPIBL
   *    Indicates whether the record was found.
   * @return
   *    True if the update was successful.
   */
   public boolean maintain_update_perform(boolean found_FAPIBL) {
      if (found_FAPIBL) {
         boolean recordChanged = !APIBL.equalsRecord(FAPIBL_record);
         if (recordChanged) {
            FAPIBL_setChanged();
            APIBL.UPDAT("00");
         } else {
            APIBL.UNLOCK("00");
         }
       } else {
         FAPIBL_setChanged();
         APIBL.setRGDT(APIBL.getLMDT());
         APIBL.setRGTM(movexTime());
         if (!APIBL.WRITE_CHK("00")) {
            // MSGID=WTR3104 Transaction number &1 already exists
            pMaintain.messages.addError(this.DSPGM, "TRNO", "WTR3104", CRCommon.formatNumForMsg(APIBL.getTRNO()));
            return false;
         }
      }
      // Change data
      pMaintain.RGDT.set(APIBL.getRGDT());
      pMaintain.LMDT.set(APIBL.getLMDT());
      pMaintain.CHID.set().moveLeftPad(APIBL.getCHID());
      return true;
   }

   /**
   * Sets access modes for parameters in interaction maintain for RETRIEVE mode.
   */
   public void maintain_setAccessModeForRetrieve() {
      // Set initial access mode for primary keys and OUT to other parameters
      pMaintain.parameters.setAllOUTAccess();
      // Transaction number
      pMaintain.TRNO.setMANDATORYAccess();
      // Disable parameters depending on invoice type.
      maintain_setDISABLEDAccessMode();
   }

   /**
   * Sets access modes for parameters in interaction maintain for ADD and CHANGE mode.
   */
   public void maintain_setAccessModeForEntry() {
      boolean vatIsCalculated = isVatCalculated(pMaintain.DIVI.get());
      // Special case in add mode if main parameters have not been set.
      // -------------------------------------------------------
      if (pMaintain.getMode() == cEnumMode.ADD && !pMaintain.work_mainParamsSet) {
         // Set access mode OUT of primary keys, MANDATORY of main parameters and
         // DISABLED of other parameters.
         pMaintain.parameters.setAllDISABLEDAccess();
         pMaintain.DIVI.setOUTAccess();
         if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
            // If not multidivision or if not central user, then the division field should not be displayed
            pMaintain.DIVI.setDISABLEDAccess();
         }
         pMaintain.INBN.setOUTAccess();
         pMaintain.SINO.setOUTAccess();
         pMaintain.SUPA.setOUTAccess();
         pMaintain.IBTP.setOUTAccess();
         pMaintain.TRNO.setOUTAccess();
         pMaintain.RDTP.setMANDATORYAccess();
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
      // - Transaction number
      pMaintain.TRNO.setOUTAccess();
      // - Line type
      pMaintain.RDTP.setOUTAccess();
      // - Net amount
      if (pMaintain.RDTP.get() != cRefRDTPext.ROUNDING_OFF()) {
         if (pMaintain.RDTP.get() != cRefRDTPext.ORDER_CHARGES() ||
              pMaintain.getMode() != cEnumMode.ADD) {
            pMaintain.NLAM.setOUTAccess();
         }
      }
      if (pMaintain.RDTP.get() == cRefRDTPext.CLAIM_LINE() &&
         LDAZZ.FPNM.EQ("PPS124")) {
         // NLAM is allowed for claim lines if coming from PPS124
         pMaintain.NLAM.setOPTIONALAccess();
      }
      if ((APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) &&
         (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() || pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE())) {
         // NLAM is allowed for Debit note item lines if coming from APMNGI07
         pMaintain.NLAM.setOPTIONALAccess();
      }
      if (LDAZZ.FPNM.EQ("PPCRTSBT")) {
         // NLAM is set when creating self billing transactions
         if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() || 
             pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE() || 
             pMaintain.RDTP.get() == cRefRDTPext.ORDER_CHARGES()) 
         {
             pMaintain.NLAM.setOPTIONALAccess();
         }
      }
      // - VAT rate 1.
      if (vatIsCalculated &&
         (pMaintain.RDTP.get() == cRefRDTPext.VAT() || pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE()) && 
          pMaintain.getMode() != cEnumMode.ADD) {
         pMaintain.VTP1.setOUTAccess(); 
      }
      // - VAT rate 2.
      if (vatIsCalculated &&
         (pMaintain.RDTP.get() == cRefRDTPext.VAT() || pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE()) && 
          pMaintain.getMode() != cEnumMode.ADD) {
         pMaintain.VTP2.setOUTAccess(); 
      }
      // - Charge text (used to retrieve cost element) 
      if ((pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE() || pMaintain.RDTP.get() == cRefRDTPext.ORDER_CHARGES()) &&
          pMaintain.getMode() != cEnumMode.ADD) {
         pMaintain.CHGT.setOUTAccess(); 
      }
      // Set MANDATORY access
      // - VAT code
      if (pMaintain.RDTP.get() == cRefRDTPext.VAT() &&
          vatIsCalculated) 
      {
         pMaintain.VTCD.setMANDATORYAccess();
      }
      // - Purchase order U/M
      if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
         pMaintain.PUUN.setOUTAccess(); 
      } else {
         if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() ||
             pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) {
            pMaintain.PUUN.setMANDATORYAccess();
         }
      }
      // - Purchase price U/M
      if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
         pMaintain.PPUN.setOUTAccess(); 
      } else {
         if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() ||
             pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) {
            pMaintain.PPUN.setMANDATORYAccess();
         }
      }
      // - Costing element
      if (pMaintain.RDTP.get() == cRefRDTPext.ORDER_CHARGES() ||
          pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) {
         pMaintain.CEID.setMANDATORYAccess();
      }
      // - Costing sequence number
      if (pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE() &&
          APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING()) {
         pMaintain.CDSE.setMANDATORYAccess();
      }
      // - Invoiced quantity 
      if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
         pMaintain.IVQA.setOUTAccess(); 
      }
      // - Gross price
      if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
         pMaintain.GRPR.setOUTAccess(); 
      } else {
         if ((pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() ||
             pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE()) &&
             APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING()) {
            // Retrieve AP settings from APS905
            retrieveAPSettings();
            // Allow invoices without amount is not set
            // if it has been set Gross price should not have been mandatory 
            if (APS905DS.getYXAIWA() == 0) {
               pMaintain.GRPR.setMANDATORYAccess();
            }   
         }
      }
      if (LDAZZ.FPNM.EQ("PPCRTSBT")) {
         // GRPR is set when creating self billing transactions
         if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() || 
             pMaintain.RDTP.get() == cRefRDTPext.LINE_CHARGE() || 
             pMaintain.RDTP.get() == cRefRDTPext.ORDER_CHARGES()) 
         {
             pMaintain.GRPR.setOPTIONALAccess();
         }
      }
      // - Item number
      if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE()) {
         pMaintain.ITNO.setMANDATORYAccess();
      }
      // - Adjusted amount
      if (pMaintain.RDTP.get() == cRefRDTPext.CLAIM_LINE()) {
         pMaintain.ADAB.setOUTAccess(); 
      }
      // - Special access for some fields on VAT line if supplier claim invoice
      if (pMaintain.RDTP.get() == cRefRDTPext.VAT() &&
         (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
          APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST()))) {
         // Gross amount
         pMaintain.GLAM.setOUTAccess(); 
         // ---
         if (LDAZZ.FPNM.NE("PPS124")) { // Entry not allowed if not supplier claim inv creation
            // VAT amount 1
            pMaintain.VTA1.setOUTAccess(); 
            // VAT amount 2
            pMaintain.VTA2.setOUTAccess(); 
            // VAT code.
            pMaintain.VTCD.setOUTAccess(); 
         }
      }
      // Disable parameters depending on invoice type.
      maintain_setDISABLEDAccessMode();
   }

   /**
   * Sets access mode DISABLED for some parameters depending on tool type.
   */
   public void maintain_setDISABLEDAccessMode() {
      boolean vatIsCalculated = isVatCalculated(pMaintain.DIVI.get());
      // Division
      if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
         // If not multidivision or if not central user, then the division field should not be displayed
         pMaintain.DIVI.setDISABLEDAccess();
      }
      // Service code
      if (pMaintain.RDTP.get() == cRefRDTPext.VAT() ||
          pMaintain.RDTP.get() == cRefRDTPext.CLAIM_LINE()) {
         pMaintain.SERS.setDISABLEDAccess(); 
      }
      // Net amount
      if (pMaintain.RDTP.get() == cRefRDTPext.VAT()) {
         pMaintain.NLAM.setDISABLEDAccess(); 
      }
      // Adjusted amount
      if (pMaintain.RDTP.get() != cRefRDTPext.CLAIM_LINE()) {
         pMaintain.ADAB.setDISABLEDAccess(); 
      }
      // Purchase order number and Claim number
      if ((APIBH.getIMCD() != cRefIMCDext.PO_LINE_MATCHING() && APIBH.getIMCD() != cRefIMCDext.PO_HEAD_MATCHING()) ||
          pMaintain.RDTP.get() == cRefRDTPext.VAT() ||
          pMaintain.RDTP.get() == cRefRDTPext.ROUNDING_OFF()) {
         pMaintain.PUNO.setDISABLEDAccess();
         pMaintain.CLAN.setDISABLEDAccess();         
      }
      // Purchase order line number and Claim line
      if ((APIBH.getIMCD() != cRefIMCDext.PO_LINE_MATCHING() && APIBH.getIMCD() != cRefIMCDext.PO_HEAD_MATCHING()) ||
          pMaintain.RDTP.get() == cRefRDTPext.ORDER_CHARGES() ||
          pMaintain.RDTP.get() == cRefRDTPext.VAT() ||
          pMaintain.RDTP.get() == cRefRDTPext.ROUNDING_OFF()) {
         pMaintain.PNLI.setDISABLEDAccess();
         pMaintain.CLLN.setDISABLEDAccess();          
      }
      // Purchase order line subnumber
      if ((APIBH.getIMCD() != cRefIMCDext.PO_LINE_MATCHING() && APIBH.getIMCD() != cRefIMCDext.PO_HEAD_MATCHING()) ||
          pMaintain.RDTP.get() == cRefRDTPext.ORDER_CHARGES() ||
          pMaintain.RDTP.get() == cRefRDTPext.VAT() ||
          pMaintain.RDTP.get() == cRefRDTPext.ROUNDING_OFF()) {
         pMaintain.PNLS.setDISABLEDAccess(); 
      }
      // VAT amount 1
      if (!vatIsCalculated || pMaintain.RDTP.get() != cRefRDTPext.VAT()) {
         pMaintain.VTA1.setDISABLEDAccess(); 
      }
      // VAT amount 2
      if (!vatIsCalculated || pMaintain.RDTP.get() != cRefRDTPext.VAT()) {
         pMaintain.VTA2.setDISABLEDAccess(); 
      }
      // Invoiced qty
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.IVQA.setDISABLEDAccess(); 
      }
      // U/M (Invoiced qty).
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.PUUN.setDISABLEDAccess(); 
      }
      // VAT code.
      if (!vatIsCalculated ||
         (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.ORDER_CHARGES() &&
          pMaintain.RDTP.get() != cRefRDTPext.VAT() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE() &&
          pMaintain.RDTP.get() != cRefRDTPext.CLAIM_LINE())) {
         pMaintain.VTCD.setDISABLEDAccess(); 
      }
      // VAT rate 1.
      if (!vatIsCalculated ||
          (pMaintain.RDTP.get() != cRefRDTPext.VAT() && pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE())) {
         pMaintain.VTP1.setDISABLEDAccess(); 
      }
      // VAT rate 2.
      if (!vatIsCalculated ||
          (pMaintain.RDTP.get() != cRefRDTPext.VAT() && pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE())) {
         pMaintain.VTP2.setDISABLEDAccess(); 
      }
      // Gross price
      if (LDAZZ.FPNM.NE("PPCRTSBT")) {
         if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
             pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
            pMaintain.GRPR.setDISABLEDAccess(); 
         }
      }
      // Purchase price U/M (Gross price).
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.PPUN.setDISABLEDAccess(); 
      }
      // Self billing agreement number.
      if (APIBH.getIBTP().NE(cRefIBTPext.SELF_BILLING()) ||
         (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.ORDER_CHARGES() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE())) {
         pMaintain.SBAN.setDISABLEDAccess(); 
      }
      // Net price
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.NEPR.setDISABLEDAccess(); 
      }
      // Sequence no
      if (pMaintain.RDTP.get() != cRefRDTPext.ORDER_CHARGES() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.CDSE.setDISABLEDAccess(); 
      }
      // Purchase order qty
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE()) {
         pMaintain.PUCD.setDISABLEDAccess(); 
      }
      // Costing element.
      if (pMaintain.RDTP.get() != cRefRDTPext.ORDER_CHARGES() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.CEID.setDISABLEDAccess(); 
      }
      // Charge text (used to retrieve cost element) 
      if (pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE() &&
          pMaintain.RDTP.get() != cRefRDTPext.ORDER_CHARGES()) {
         pMaintain.CHGT.setDISABLEDAccess(); 
      }
      // Gross amount
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.VAT() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.GLAM.setDISABLEDAccess(); 
      }
      // Receiving number.
      if (APIBH.getIBTP().NE(cRefIBTPext.SELF_BILLING()) &&
          pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) 
      {
         pMaintain.REPN.setDISABLEDAccess(); 
      }
      if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING()) &&
          pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE() &&
          pMaintain.RDTP.get() != cRefRDTPext.ORDER_CHARGES())
      {
         pMaintain.REPN.setDISABLEDAccess(); 
      }
      // Discount
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.DIPC.setDISABLEDAccess(); 
      }
      // Receipt type
      if (APIBH.getIBTP().NE(cRefIBTPext.SELF_BILLING()) &&
          pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) 
      {
         pMaintain.RELP.setDISABLEDAccess(); 
      }
      if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING()) &&
          pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE() &&
          pMaintain.RDTP.get() != cRefRDTPext.ORDER_CHARGES())
      {
         pMaintain.RELP.setDISABLEDAccess(); 
      }
      // Discount amount
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.DIAM.setDISABLEDAccess(); 
      }
      // Deliver note number.
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.SUDO.setDISABLEDAccess(); 
      }
      // Delivery note date
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.DNDT.setDISABLEDAccess(); 
      }
      // Item number
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.ITNO.setDISABLEDAccess(); 
      }
      // Invoiced catch weight.
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         found_MITMAS = cRefITNOext.getMITMAS(ITMAS, found_MITMAS, currentCONO, pMaintain.ITNO.get());
         if (!found_MITMAS ||
             ITMAS.getACTI() < 2) {
            pMaintain.IVCW.setDISABLEDAccess();
         }
      }
      // Alias number.
      if (pMaintain.RDTP.get() != cRefRDTPext.ITEM_LINE() &&
          pMaintain.RDTP.get() != cRefRDTPext.LINE_CHARGE()) {
         pMaintain.POPN.setDISABLEDAccess(); 
      }
   }

  /**
   * Returns true if line number in MPLINE is found for current item
   */
   public boolean maintain_getLineNoFromMPLINE() {
      int returnValuePNLI = 0;
      int returnValuePNLS = 0;
      boolean endLoop = false;
      if (pMaintain.RDTP.get() == cRefRDTPext.ITEM_LINE() &&
          !pMaintain.PUNO.isBlank() &&
          !pMaintain.ITNO.isBlank() &&
          pMaintain.PNLI.get() == 0 &&
          APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING()) {
         PLINE.setCONO(currentCONO);
         PLINE.setPUNO().move(pMaintain.PUNO.get());
         PLINE.setPNLI(0);
         PLINE.setPNLS(0);
         PLINE.SETLL("00", PLINE.getKey("00", 4));
         while (PLINE.READE("00", PLINE.getKey("00", 2)) && 
                !endLoop) {
            if (PLINE.getITNO().EQ(pMaintain.ITNO.get()) &&
                PLINE.getPUSL().LT("85")) {
               // Item number is found
               returnValuePNLI = PLINE.getPNLI();
               returnValuePNLS = PLINE.getPNLS();
               // Same Item must not exist more than once
               while (PLINE.READE("00", PLINE.getKey("00", 2)) && 
                      !endLoop) {
                  if (PLINE.getITNO().EQ(pMaintain.ITNO.get()) &&
                      PLINE.getPUSL().LT("85")) {
                     // Same item found again, return 0, as we can not know which one to choose
                     returnValuePNLI = 0;
                     returnValuePNLS = 0;
                     endLoop = true;
                  }
               }
               endLoop = true;
            }
         }
      }   
      pMaintain.PNLI.set(returnValuePNLI);
      pMaintain.PNLS.set(returnValuePNLS);
      if (pMaintain.PNLI.get() != 0) {
         return true;
      } else {
         return false;
      }
   }

   /**
   * Interaction delete - step INITIATE.
   */
   public void do_delete_initiate() {
      // Get record
      // =========================================
      if (pDelete.passFAPIBL) {
         // Move record from calling program
         moveFAPIBL(APIBL, pDelete.APIBL);
         // Set primary keys
         pDelete.DIVI.set().moveLeftPad(APIBL.getDIVI());
         pDelete.INBN.set(APIBL.getINBN());
         pDelete.TRNO.set(APIBL.getTRNO());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pDelete.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         pDelete.INBN.validateMANDATORYandConstraints();
         // Transaction number
         pDelete.TRNO.validateMANDATORYandConstraints();
         // Return error messages
         if (pDelete.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pDelete.parameters.setAllDISABLEDAccess();
            pDelete.DIVI.setOUTAccess();
            pDelete.INBN.setOUTAccess();
            pDelete.TRNO.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBL.setCONO(currentCONO);
         APIBL.setDIVI().move(pDelete.DIVI.get());
         APIBL.setINBN(pDelete.INBN.get());
         APIBL.setTRNO(pDelete.TRNO.get());
         if (!APIBL.CHAIN("00", APIBL.getKey("00"))) {
            // MSGID=WTR3103 Transaction number &1 does not exist
            pDelete.messages.addError(this.DSPGM, "TRNO", "WTR3103", CRCommon.formatNumForMsg(APIBL.getTRNO()));
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pDelete.parameters.setAllDISABLEDAccess();
            pDelete.DIVI.setOUTAccess();
            pDelete.INBN.setOUTAccess();
            pDelete.TRNO.setOUTAccess();
            return;
         }
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pDelete.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pDelete.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Check status
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, currentCONO, pDelete.DIVI.get(), pDelete.INBN.get());
      if (!found_FAPIBH ||
          APIBH.getSUPA() != cRefSUPAext.NEW()) {
         // MSGID=XST0013 Delete is not permitted due to status 
         pDelete.messages.addError(this.DSPGM, "TRNO", "XST0013");
      }
      if (LDAZZ.FPNM.NE("PPS124")) {
         if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
             APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
            // MSGID=XDE0008 Delete is not allowed for for invoice type &1.
            pDelete.messages.addError(this.DSPGM, "TRNO", "XDE0008", APIBH.getIBTP());
         }
      }
      // Check if Invoice batch number is Work in progress
      checkInvoiceForWIP(pDelete.DIVI.get(), pDelete.INBN.get(), pDelete.messages);
      if (pDelete.messages.existError()) {
         // Set key parameters to access mode OUT and other parameters to DISABLED.
         pDelete.parameters.setAllDISABLEDAccess();
         pDelete.DIVI.setOUTAccess();
         pDelete.INBN.setOUTAccess();
         pDelete.TRNO.setOUTAccess();
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
      // MSGID=WINBN05 Confirm deletion of transaction number &1
      pDelete.messages.addNotification(this.DSPGM, "TRNO", "WTR3105", CRCommon.formatNumForMsg(pDelete.TRNO.get()));
   }

   /**
   * Sets parameters in step INITIATE of interaction delete.
   */
   public void delete_initiate_setParameters() {
      // Line type
      pDelete.RDTP.set(APIBL.getRDTP());
      // Supplier invoice number
      pDelete.SINO.set().moveLeftPad(APIBH.getSINO());
      // Payee
      pDelete.SPYN.set().moveLeftPad(APIBH.getSPYN());
      // Supplier
      pDelete.SUNO.set().moveLeftPad(APIBH.getSUNO());
      // Net amount
      pDelete.NLAM.set(APIBL.getNLAM());
      // Get decimals
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, currentCONO, pDelete.DIVI.get(), pDelete.INBN.get());
      if (found_FAPIBH) {
         found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, APIBH.getCUCD());
         if (found_CSYTAB_CUCD) {
            cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
            pDelete.NLAM.setDecimals(DSCUCD.getYQDCCD());
         }
      }
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
   * Perform explicit transaction APS451FncINdelete
   */
   @Transaction(name=cPXAPS451FncINdelete.LOGICAL_NAME, primaryTable="FAPIBL") 
   public void transaction_APS451FncINdelete() {
      // Declaration
      boolean alreadyLocked = false;
      // Set Invoice batch number to status - update in progress
      if (!lockInvoiceForUpdate(pDelete.DIVI.get(), pDelete.INBN.get(), pDelete.messages, alreadyLockedByJob, -1)) {
         return;
      }
      alreadyLocked = alreadyLockedByJob.getBoolean();
      // Check if delete is possible
      APIBL.setCONO(currentCONO);
      APIBL.setDIVI().moveLeftPad(pDelete.DIVI.get());
      APIBL.setINBN(pDelete.INBN.get());
      APIBL.setTRNO(pDelete.TRNO.get());
      if (APIBL.CHAIN_LOCK("00", APIBL.getKey("00"))) {
         // Perform update
         // =========================================
         delete_update_perform();
      }
      // Set Invoice batch number to status - Unlocked
      if (!alreadyLocked) {
         unlockInvoice(pDelete.DIVI.get(), pDelete.INBN.get());
      }
   }

   /**
   * Effectuate update in interaction delete.
   */
   public void delete_update_perform() {
      // Delete text
      if (APIBL.getTXID() != 0L) {
         this.PXCONO = currentCONO;
         this.PXDIVI.clear();
         this.PXFTXH.moveLeft("FSYTXH00");
         this.PXFTXL.moveLeft("FSYTXL00");
         this.PXTXID = APIBL.getTXID();
         PXCRS98X.CRS984();
      }
      savedVTA1 = APIBL.getVTA1();
      savedVTA2 = APIBL.getVTA2();
      // Delete record in FAPIBL
      APIBL.DELET("00");
      if (LDAZZ.FPNM.NE("PPS124")) {
         // Adjust accumulated amounts in header record
         APIBH.setCONO(currentCONO);
         APIBH.setDIVI().moveLeftPad(pDelete.DIVI.get());
         APIBH.setINBN(pDelete.INBN.get());
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
         if (found_FAPIBH) {
            // VAT amount
            if (!equals(savedVTA1, 0d, cRefVTA1.decimals()) ||
                !equals(savedVTA2, 0d, cRefVTA2.decimals())) {
               APIBH.setVTAM(mvxHalfAdjust(APIBH.getVTAM() - savedVTA1 - savedVTA2, cRefVTAM.decimals()));
            }
            // Total line amount
            if (APIBL.getRDTP() != cRefRDTPext.VAT()) {
               APIBH.setTLNA(APIBH.getTLNA() - APIBL.getNLAM());
               APIBH.setTLNA(mvxHalfAdjust(APIBH.getTLNA(), cRefTLNA.decimals()));
            }
         }
         FAPIBH_setChanged();
         APIBH.UPDAT("00");
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
      if (pCopy.passFAPIBL) {
         // Move record from calling program
         moveFAPIBL(APIBL, pCopy.APIBL);
         // Set primary keys
         pCopy.DIVI.set().moveLeftPad(APIBL.getDIVI());
         pCopy.INBN.set(APIBL.getINBN());
         pCopy.TRNO.set(APIBL.getTRNO());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pCopy.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         pCopy.INBN.validateMANDATORYandConstraints();
         // Transaction number
         pCopy.TRNO.validateMANDATORYandConstraints();
         // Return error messages
         if (pCopy.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pCopy.parameters.setAllDISABLEDAccess();
            pCopy.DIVI.setOUTAccess();
            pCopy.INBN.setOUTAccess();
            pCopy.TRNO.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBL.setCONO(currentCONO);
         APIBL.setDIVI().move(pCopy.DIVI.get());
         APIBL.setINBN(pCopy.INBN.get());
         APIBL.setTRNO(pCopy.TRNO.get());
         if (!APIBL.CHAIN("00", APIBL.getKey("00"))) {
            // MSGID=WTR3103 Transaction number &1 does not exist
            pCopy.messages.addError(this.DSPGM, "TRNO", "WTR3103", CRCommon.formatNumForMsg(APIBL.getTRNO()));
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pCopy.parameters.setAllDISABLEDAccess();
            pCopy.DIVI.setOUTAccess();
            pCopy.INBN.setOUTAccess();
            pCopy.TRNO.setOUTAccess();
            return;
         }
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pCopy.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pCopy.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Check status
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, currentCONO, pCopy.DIVI.get(), pCopy.INBN.get());
      if (!found_FAPIBH ||
          APIBH.getSUPA() != cRefSUPAext.NEW()) {
         // MSGID=XST0012 Change is not permitted due to status 
         pCopy.messages.addError(this.DSPGM, "TRNO", "XST0012");
      }
      // Check if Invoice batch number is Work in progress
      checkInvoiceForWIP(pCopy.DIVI.get(), pCopy.INBN.get(), pCopy.messages);
      if (pCopy.messages.existError()) {
         // Set key parameters to access mode OUT and other parameters to DISABLED.
         pCopy.parameters.setAllDISABLEDAccess();
         pCopy.DIVI.setOUTAccess();
         pCopy.INBN.setOUTAccess();
         pCopy.TRNO.setOUTAccess();
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
      pCopy.CPDIVI.set().moveLeftPad(APIBL.getDIVI());
      // Copy to Invoice batch number
      pCopy.CPINBN.set(APIBL.getINBN());
      // Copy to Transaction number will be set automatically
      pCopy.CPTRNO.clearValue();
   }

   /**
   * Interaction copy - step VALIDATE.
   */
   public void do_copy_validate() {
      // Validate parameters
      // =========================================
      //Nothing to validate as a new Transaction number will be fetched for the new record
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
   * Perform explicit transaction APS451FncINcopy
   */
   @Transaction(name=cPXAPS451FncINcopy.LOGICAL_NAME, primaryTable="FAPIBL") 
   public void transaction_APS451FncINcopy() {
      // Declaration
      boolean alreadyLocked = false;
      // Set Invoice batch number to status - Update in progress
      if (!lockInvoiceForUpdate(pCopy.DIVI.get(), pCopy.INBN.get(), pCopy.messages, alreadyLockedByJob, -1)) {
         return;
      }
      alreadyLocked = alreadyLockedByJob.getBoolean();
      // Fetch next transaction number
      APIBL.setCONO(currentCONO);
      APIBL.setDIVI().move(pCopy.DIVI.get());
      APIBL.setINBN(pCopy.INBN.get());
      APIBL.SETGT("00", APIBL.getKey("00", 3));
      if (!APIBL.REDPE("00", APIBL.getKey("00", 3))) {
         nextTRNO = 1;
      } else {
         nextTRNO = APIBL.getTRNO() + 1;
      }
      // Check if copy is possible
      // =========================================
      APIBL.setCONO(currentCONO);
      APIBL.setDIVI().move(pCopy.DIVI.get());
      APIBL.setINBN(pCopy.INBN.get());
      APIBL.setTRNO(pCopy.TRNO.get());
      // Copy of deleted record
      if (!APIBL.CHAIN("00", APIBL.getKey("00"))) {
         // MSGID=XDE0001 The record has been deleted by another user
         pCopy.messages.addError(this.DSPGM, "TRNO", "XDE0001");
         if (!alreadyLocked) {
            // Set Invoice batch number to status - Unlocked
            unlockInvoice(pCopy.DIVI.get(), pCopy.INBN.get());
         }
         return;
      }
      // Perform copy
      // =========================================
      if (!copy_update_perform()) {
         if (!alreadyLocked) {
            // Set Invoice batch number to status - Unlocked
            unlockInvoice(pCopy.DIVI.get(), pCopy.INBN.get());
         }
         return;
      }
      if (!alreadyLocked) {
         // Set Invoice batch number to status - Unlocked
         unlockInvoice(pCopy.DIVI.get(), pCopy.INBN.get());
      }
   }

   /**
   * Effectuate update in interaction copy.
   * @return
   *    True if the update was successful.
   */
   public boolean copy_update_perform() {
      APIBL.setDIVI().move(pCopy.CPDIVI.get());
      APIBL.setINBN(pCopy.CPINBN.get());
      APIBL.setTRNO(nextTRNO);
      pCopy.CPTRNO.set(nextTRNO);
      FAPIBL_setChanged();
      APIBL.setRGDT(APIBL.getLMDT());
      APIBL.setRGTM(movexTime());
      if (!APIBL.WRITE_CHK("00")) {
         // MSGID=WTR3104 Transaction number &1 already exists
          pCopy.messages.addError(this.DSPGM, "CPTRNO", "WTR3104", CRCommon.formatNumForMsg(nextTRNO));
         return false;
      }
      // Copy text
      if (APIBL.getTXID() != 0L) {
         this.PXCONO = currentCONO;
         this.PXDIVI.clear();
         this.PXFTXH.moveLeft("FSYTXH00");
         this.PXFTXL.moveLeft("FSYTXL00");
         DSINBN.move(APIBL.getINBN());
         DSTRNO.move(APIBL.getTRNO());
         this.PXKFLD.moveLeftPad(DSKFLD);
         this.PXFILE.moveLeft("FAPIBL00");
         this.PXTXID = APIBL.getTXID();
         this.PXLNCD.clear();
         this.PXTXVR.clear();
         this.PXPICC.clear();
         PXCRS98X.CRS983();
         if (APIBL.CHAIN_LOCK("00", APIBL.getKey("00"))) {
            APIBL.setTXID(this.PXTXID);
            APIBL.UPDAT("00");
         }
      }
      if (LDAZZ.FPNM.NE("PPS124")) {
         // Adjust accumulated amounts in header record
         APIBH.setCONO(currentCONO);
         APIBH.setDIVI().moveLeftPad(pCopy.CPDIVI.get());
         APIBH.setINBN(pCopy.CPINBN.get());
         found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
         if (found_FAPIBH) {
            if (!equals(APIBL.getVTA1(), 0d, cRefVTA1.decimals()) ||
                !equals(APIBL.getVTA2(), 0d, cRefVTA2.decimals())) {
               // VAT amount
               APIBH.setVTAM(mvxHalfAdjust(APIBH.getVTAM() + APIBL.getVTA1() + APIBL.getVTA2(), cRefVTAM.decimals()));
            }
            // Total line amount
            if (APIBL.getRDTP() != cRefRDTPext.VAT()) {
               APIBH.setTLNA(APIBH.getTLNA() + APIBL.getNLAM());
               APIBH.setTLNA(mvxHalfAdjust(APIBH.getTLNA(), cRefTLNA.decimals()));
            }
         }
         FAPIBH_setChanged();
         APIBH.UPDAT("00");
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
      pCopy.CPINBN.setOUTAccess();
      // - Transaction number
      pCopy.TRNO.setOUTAccess();
      pCopy.CPTRNO.setOUTAccess();
   } 

   /**
   * Interaction Adjust Line - step INITIATE.
   */
   public void do_adjustLine_initiate() {
      // Get record
      // =========================================
      if (pAdjustLine.passFAPIBL) {
         // Move record from calling program
         moveFAPIBL(APIBL, pAdjustLine.APIBL);
         // Set primary keys
         pAdjustLine.DIVI.set().moveLeftPad(APIBL.getDIVI());
         pAdjustLine.INBN.set(APIBL.getINBN());
         pAdjustLine.TRNO.set(APIBL.getTRNO());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pAdjustLine.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         pAdjustLine.INBN.validateMANDATORYandConstraints();
         // Transaction number
         pAdjustLine.TRNO.validateMANDATORYandConstraints();
         // Return error messages
         if (pAdjustLine.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pAdjustLine.parameters.setAllDISABLEDAccess();
            pAdjustLine.DIVI.setOUTAccess();
            pAdjustLine.INBN.setOUTAccess();
            pAdjustLine.TRNO.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBL.setCONO(currentCONO);
         APIBL.setDIVI().move(pAdjustLine.DIVI.get());
         APIBL.setINBN(pAdjustLine.INBN.get());
         APIBL.setTRNO(pAdjustLine.TRNO.get());
         if (!APIBL.CHAIN("00", APIBL.getKey("00"))) {
            // MSGID=WTR3103 Transaction number &1 does not exist
            pAdjustLine.messages.addError(this.DSPGM, "TRNO", "WTR3103", CRCommon.formatNumForMsg(APIBL.getTRNO()));
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pAdjustLine.parameters.setAllDISABLEDAccess();
            pAdjustLine.DIVI.setOUTAccess();
            pAdjustLine.INBN.setOUTAccess();
            pAdjustLine.TRNO.setOUTAccess();
            return;
         }
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pAdjustLine.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pAdjustLine.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      // Adjust line
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, currentCONO, pAdjustLine.DIVI.get(), pAdjustLine.INBN.get());
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Invoice batch number &1 does not exist
         pAdjustLine.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
      }
      if (!APS450Fnc.statusOKForAdjLine(APIBH)) {
         // X__8502 = Adjust invoice
         messageData_X_00051_TEXT.moveLeftPad(formatToString(SRCOMRCM.getMessage("X__8502", "MVXCON")));
         messageData_X_00051_OPT2.moveLeftPad("20");
         messageData_X_00051_SUPA.moveLeftPad(CRCommon.formatNumForMsg(APIBH.getSUPA()));
         // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
         pAdjustLine.messages.addError(this.DSPGM, "INBN", "X_00051", messageData_X_00051);
      }
      if (APIBL.getRDTP() != cRefRDTPext.CLAIM_LINE()) {
         // MSGID=AP_0365 Option &1 is not allowed for line type &2
         messageData_AP_0365_OPT2.moveLeftPad("20");
         messageData_AP_0365_RDTP.moveLeft(APIBL.getRDTP(), 1);
         pAdjustLine.messages.addError(this.DSPGM, "TRNO", "AP_0365", messageData_AP_0365);
      }
      // Check if Invoice batch number is Work in progress
      checkInvoiceForWIP(pAdjustLine.DIVI.get(), pAdjustLine.INBN.get(), pAdjustLine.messages);
      if (pAdjustLine.messages.existError()) {
         // Set key parameters to access mode OUT
         // and other parameters to DISABLED.
         pAdjustLine.parameters.setAllDISABLEDAccess();
         pAdjustLine.DIVI.setOUTAccess();
         pAdjustLine.INBN.setOUTAccess();
         pAdjustLine.TRNO.setOUTAccess();
         return;
      }
      // Set parameters
      // =========================================
      adjustLine_initiate_setParameters();
      // Set Access mode
      // =========================================
      adjustLine_setAccessMode();
   }

   /**
   * Sets parameters in step INITIATE of interaction AdjustLine.
   */
   public void adjustLine_initiate_setParameters() {
      // Invoice batch type
      pAdjustLine.IBTP.set().moveLeftPad(APIBH.getIBTP());
      // Status
      pAdjustLine.SUPA.set(APIBH.getSUPA());
      // Supplier invoice number
      pAdjustLine.SINO.set().moveLeftPad(APIBH.getSINO());
      // Line type
      pAdjustLine.RDTP.set(APIBL.getRDTP());
      // Line error
      pAdjustLine.IBLE.set(APIBL.getIBLE());
      // Message
      if (!APIBL.getMSID().isBlank()) {
         COMRTM(APIBL.getMSID().toString(), "MVXMSG", APIBL.getMSGD().toString()); 	
         pAdjustLine.MSGD.set().moveLeftPad(SRCOMRCM.MSG);
      } else {
         pAdjustLine.MSGD.clearValue();
      }
      // VAT code
      pAdjustLine.VTCD.set(APIBL.getVTCD());
      // Net amount.
      pAdjustLine.NLAM.set(APIBL.getNLAM());
      // Adjusted amount
      pAdjustLine.ADAB.set(APIBL.getADAB());
      // Currency
      pAdjustLine.CUCD.set().moveLeftPad(APIBH.getCUCD());
      // Set number of decimals
      if (!APIBH.getCUCD().isBlank()) {
         found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, currentCONO, APIBH.getCUCD());
         if (found_CSYTAB_CUCD) {
            cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
            pAdjustLine.NLAM.setDecimals(DSCUCD.getYQDCCD());
            pAdjustLine.ADAB.setDecimals(DSCUCD.getYQDCCD());
         }
      }
      // Entry date
      pAdjustLine.RGDT.set(APIBL.getRGDT());
      // Change date
      pAdjustLine.LMDT.set(APIBL.getLMDT());
      // Change ID
      pAdjustLine.CHID.set().moveLeftPad(APIBL.getCHID());
      // Change number
      pAdjustLine.work_CHNO = APIBL.getCHNO();
   }

   /**
   * Interaction AdjustLine - step VALIDATE.
   */
   public void do_adjustLine_validate() {
      // Validate parameters
      // Net amount
      pAdjustLine.NLAM.set(mvxHalfAdjust(pAdjustLine.NLAM.get(), pAdjustLine.NLAM.getDecimals()));
      pAdjustLine.NLAM.validateMANDATORYandConstraints();
      // Adjusted amount
      pAdjustLine.ADAB.set(mvxHalfAdjust(pAdjustLine.ADAB.get(), pAdjustLine.ADAB.getDecimals()));
      pAdjustLine.ADAB.validateMANDATORYandConstraints();
      if (greaterThan(pAdjustLine.ADAB.get(), cRefADAB.decimals(), 0d)) {
         // MSGID=XNU0011 Positive value is not permitted
         pAdjustLine.messages.addError(this.DSPGM, "ADAB", "XNU0011");
      }
      // VAT code
      pAdjustLine.VTCD.validateMANDATORYandConstraints();
      if (!pAdjustLine.VTCD.isAccessDISABLED()) {
         if (!pAdjustLine.VTCD.isBlank()) {
            found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
            found_CSYTAB_VTCD = cRefVTCDext.getCSYTAB_VTCD(SYTAB, found_CSYTAB_VTCD, MNCMP.getCMTP(), currentCONO, pAdjustLine.DIVI.get(), pAdjustLine.VTCD.get());
            pAdjustLine.VTCD.validateExists(found_CSYTAB_VTCD);
         }
      }
   }

   /**
   * Interaction AdjustLine - step UPDATE.
   */
   public void do_adjustLine_update() {
      executeTransaction(pAdjustLine.getTransactionName());
      if (pAdjustLine.messages.existError()) {
         return;
      }
   }

   /**
   * Perform explicit transaction APS451FncINadjustLine
   */
   @Transaction(name=cPXAPS451FncINadjustLine.LOGICAL_NAME, primaryTable="FAPIBL") 
   public void transaction_APS451FncINadjustLine() {
      // Declaration
      boolean alreadyLocked = false;
      boolean found_FAPIBL = false;
      // Set Invoice batch number to status - Update in progress
      if (!lockInvoiceForAdjustLine(pAdjustLine.DIVI.get(), pAdjustLine.INBN.get(), pAdjustLine.messages, alreadyLockedByJob)) {
         return;
      }
      alreadyLocked = alreadyLockedByJob.getBoolean();
      APIBL.setCONO(currentCONO);
      APIBL.setDIVI().moveLeftPad(pAdjustLine.DIVI.get());
      APIBL.setINBN(pAdjustLine.INBN.get());
      APIBL.setTRNO(pAdjustLine.TRNO.get());
      found_FAPIBL = APIBL.CHAIN_LOCK("00", APIBL.getKey("00"));
      // Lock FAPIBL for update
      if (!found_FAPIBL) {
         // MSGID=WTR3103 Transaction number &1 does not exist
         pAdjustLine.messages.addError(this.DSPGM, "TRNO", "WTR3103", CRCommon.formatNumForMsg(APIBL.getTRNO()));
         // Set Invoice batch number to status - Unlocked
         if (!alreadyLocked) {
            unlockInvoice(pAdjustLine.DIVI.get(), pAdjustLine.INBN.get());
         }
         return;
      }
      // Update of changed record
      if (found_FAPIBL && pAdjustLine.work_CHNO != APIBL.getCHNO()) {
         APIBL.UNLOCK("00");
         // MSGID=XUP0001 The record has been changed by user &1
         pAdjustLine.messages.addError(this.DSPGM, "", "XUP0001", APIBL.getCHID());
         // Set Invoice batch number to status - Unlocked
         if (!alreadyLocked) {
            unlockInvoice(pAdjustLine.DIVI.get(), pAdjustLine.INBN.get());
         }
         return;
      }
      // Move parameters to DB-record
      // =========================================
      adjustLine_update_setValues();
      // Perform update
      // =========================================
      adjustLine_update_perform();
      if (!alreadyLocked) {
         unlockInvoice(pAdjustLine.DIVI.get(), pAdjustLine.INBN.get());
      }
   }

   /**
   * Sets database field values in step UPDATE of interaction Approve.
   * Moves values from the parameter list to the database fields.
   */
   public void adjustLine_update_setValues() {
      APIBL.setADAB(pAdjustLine.ADAB.get());
      APIBL.setVTCD(pAdjustLine.VTCD.get());
   }

   /**
   * Effectuate update in interaction adjAppr.
   */
   public void adjustLine_update_perform() {
      double accumulatedADAB = 0d;
      double usedAmount = 0d;
      boolean firstUpdate = true;
      FAPIBL_setChanged();
      APIBL.UPDAT("00");
      // Change data
      pAdjustLine.RGDT.set(APIBL.getRGDT());
      pAdjustLine.LMDT.set(APIBL.getLMDT());
      pAdjustLine.CHID.set().moveLeftPad(APIBL.getCHID());
      // Find and update VAT line
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, currentCONO, pAdjustLine.DIVI.get(), pAdjustLine.INBN.get());
      APIBL.setRDTP(cRefRDTPext.CLAIM_LINE());
      APIBL.SETLL("10", APIBL.getKey("10", 4));
      while (APIBL.READE("10", APIBL.getKey("10", 4))) {
         // Accumulate Claim lines
         accumulatedADAB = accumulatedADAB + APIBL.getADAB();
         usedAmount = APIBL.getADAB();
         if (APIBL.getVTCD() == pAdjustLine.VTCD.get()) {
            //   Calculate VAT
            PLCRTVT.FTCONO = APIBL.getCONO();
            PLCRTVT.FTCMTP = MNCMP.getCMTP(); 
            PLCRTVT.FTDIVI.move(APIBL.getDIVI());
            PLCRTVT.FTTASK = 1;
            PLCRTVT.FTVATH = 1;
            PLCRTVT.FTVTCD = APIBL.getVTCD();
            PLCRTVT.FTCSCD.move(APIBH.getFTCO());     
            PLCRTVT.FTECAR.move(APIBH.getECAR());
            PLCRTVT.FTSUNO.move(APIBH.getSUNO());
            PLCRTVT.FTIOCD = 2;
            PLCRTVT.FTACDT = movexDate();
            PLCRTVT.FTLCDC = pAdjustLine.ADAB.getDecimals();
            PLCRTVT.FTBAAM =  usedAmount;
            PLCRTVT.FTCUCD.move(APIBH.getCUCD());
            IN92 = PLCRTVT.CCRTVAT();
            if (PLCRTVT.FTVERR == 1) {

            } else {
               //   Save record contents from FAPIBL
               FAPIBL_record.setRecord(APIBL);
               APIBL.setRDTP(cRefRDTPext.VAT());
               APIBL.SETLL("10", APIBL.getKey("10", 4));
               while (APIBL.READE("10", APIBL.getKey("10", 4))) {
                  if (APIBL.getVTCD() == pAdjustLine.VTCD.get()) {
                     // Update FAPIBL with new VAT
                     if (APIBL.CHAIN_LOCK("00", APIBL.getKey("00"))) {
                        if (firstUpdate) {
                           firstUpdate = false;
                           APIBL.setNLAM(mvxHalfAdjust(PLCRTVT.FTVTM1 + PLCRTVT.FTVTM2, cRefNLAM.decimals()));
                           APIBL.setVTA1(mvxHalfAdjust(PLCRTVT.FTVTM1, cRefVTA1.decimals()));
                           APIBL.setVTA2(mvxHalfAdjust(PLCRTVT.FTVTM2, cRefVTA2.decimals()));
                        } else {
                           APIBL.setNLAM(mvxHalfAdjust(APIBL.getNLAM() + PLCRTVT.FTVTM1 + PLCRTVT.FTVTM2, cRefNLAM.decimals()));
                           APIBL.setVTA1(mvxHalfAdjust(APIBL.getVTA1() + PLCRTVT.FTVTM1, cRefVTA1.decimals()));
                           APIBL.setVTA2(mvxHalfAdjust(APIBL.getVTA2() + PLCRTVT.FTVTM2, cRefVTA2.decimals()));
                        }
                        FAPIBL_setChanged();
                        APIBL.UPDAT("00");
                     }
                     break;
                  }
               }
               //   Restore currenct FAPIBL record information
               APIBL.setRecord(FAPIBL_record);
               APIBL.SETGT("10", APIBL.getKey("10"));
            }
         }
      }
      // Accumulate VAT lines as well
      APIBL.setRDTP(cRefRDTPext.VAT());
      APIBL.SETLL("10", APIBL.getKey("10", 4));
      while (APIBL.READE("10", APIBL.getKey("10", 4))) {
         accumulatedADAB = accumulatedADAB + APIBL.getNLAM();
      }
      // Update FAPIBH with accumulated adjustment
      APIBH.setCONO(currentCONO);
      APIBH.setDIVI().moveLeftPad(APIBL.getDIVI());
      APIBH.setINBN(APIBL.getINBN());
      found_FAPIBH = APIBH.CHAIN_LOCK("00", APIBH.getKey("00"));
      if (found_FAPIBH) {
         if (APIBH.getRPAA() == 1) {
            APIBH.setSUPA(cRefSUPAext.ADJUSTED_NOT_REPRINTED());
         } else {
            APIBH.setSUPA(cRefSUPAext.ADJUSTED());
         }
         // Adjusted amount
         APIBH.setADAB(mvxHalfAdjust(accumulatedADAB, cRefADAB.decimals()));
         FAPIBH_setChanged();
         APIBH.UPDAT("00");
      }
   }

   /**
   * Sets access mode for interaction adjAppr.
   */
   public void adjustLine_setAccessMode() {
      // Set access mode OUT to all fields
      pAdjustLine.parameters.setAllOUTAccess();
      // Division
      if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
         // If not multidivision or if not central user, then the division field should not be displayed
         pAdjustLine.DIVI.setDISABLEDAccess();
      }
      // Set access mode for Adjusted amount
      pAdjustLine.ADAB.setOPTIONALAccess();
      // Set access mode for VAT code
      pAdjustLine.VTCD.setOUTAccess();
   }   

   /**
   * Interaction settings - step INITIATE.
   */
   public void do_settings_initiate() {
      // Validate primary key parameters
      // =========================================
      // Tool
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
      SYSTR.setPGNM().moveLeftPad("APS451");
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
   * Perform explicit transaction APS451FncINsettings
   */
   @Transaction(name=cPXAPS451FncINsettings.LOGICAL_NAME, primaryTable="CSYSTR") 
   public void transaction_APS451FncINsettings() {
      // Declaration
      boolean found_CSYSTR = false;
      // Check if update is possible
      // =========================================
      SYSTR.setCONO(currentCONO);
      SYSTR.setDIVI().clear();
      SYSTR.setPGNM().moveLeftPad("APS451");
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
   public void FAPIBL_setChanged() {
      APIBL.setLMDT(movexDate());
      APIBL.setCHID().move(this.DSUSS);
      APIBL.setCHNO(APIBL.getCHNO() + 1);
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
   public void moveFAPIBL(mvx.db.dta.FAPIBL to, mvx.db.dta.FAPIBL from) {
      FAPIBL_record.reset();
      FAPIBL_record.setMDBRecord(from);
      FAPIBL_record.reset();
      FAPIBL_record.getMDBRecord(to);
   }

   /**
   * Interaction maintain - step VALIDATE.
   */
   public void setDynamicCaptionGLAM() {
      // Caption for VAT line is 'VAT rec amount' for others 'Gross amount'
      if (pMaintain.RDTP.get() == cRefRDTPext.VAT()) {
         pMaintain.GLAM.setCaption(VAT_REC_AMOUNT_CAPTION);
      } else {
         pMaintain.GLAM.setCaption(GROSS_AMOUNT_CAPTION);
      }
   }

   /**
    * Finds item number from alias. If alias is not found, the alias value is returned.
    * @return 
    * True if ITNO is populated from POPN (alias)
    */
   public boolean getItnoFromAlias() {
      // Item number.
      if (pMaintain.ITNO.isAccessMANDATORYorOPTIONAL()) {
         if (pMaintain.ITNO.get().isBlank() &&
            !pMaintain.POPN.get().isBlank()) {
            pMaintain.ITNO.set().moveLeftPad(pMaintain.POPN.get());
            ITVEN.setCONO(currentCONO);
            ITVEN.setSITE().moveLeftPad(pMaintain.POPN.get());
            ITVEN.setSUNO().moveLeftPad(APIBH.getSUNO());
            if (ITVEN.CHAIN("20", ITVEN.getKey("20", 3))) {
               if (!ITVEN.getITNO().isBlank()) {
                  pMaintain.ITNO.set().moveLeftPad(ITVEN.getITNO());
               }
            }
            return true;
         }
      }
      return false;
   }
   
   /**
   * Checks if VAT is calculated for the given Division.
   * @param DIVI
   *    Division
   * @return 
   *    True if VAT on supplier invoice should be calculated.
   */
   public boolean isVatCalculated(MvxString DIVI) {
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, currentCONO, DIVI);
      if (MNDIV.getTATM() == 1 ||
          MNDIV.getTATM() == 4) {
         return true;
      } else {
         return false;
      }
   }

   /** 
   * Retrieve VAT rate.
   *   The VAT rate is retrieved in the following sequence 	 	
   *   visualized in the matrix below.
   * <PRE>
   *   *---------------------------------------------------* 	 	
   *   | Case | Division | VAT code | Country | Area/State | 	 	
   *   | ---- | -------- | -------- | ------- | ---------- | 	 	
   *   | 1A   | DIVI     | VTCD     | CSCD    | ECAR       | 	 	
   *   | 1B   | blank    | VTCD     | CSCD    | ECAR       | 	 	
   *   | 2A   | DIVI     | VTCD     | CSCD    | blank      | 	 	
   *   | 2B   | blank    | VTCD     | CSCD    | blank      | 	 	
   *   | 3A   | DIVI     | VTCD     | blank   | blank      | 	 	
   *   | 3B   | blank    | VTCD     | blank   | blank      | 	 	
   *   *---------------------------------------------------* 	
   * </PRE>
   * @return 
   *    True if VAT rate has ben found.
   */
   public boolean retrieveVatRate() {
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, currentCONO, pMaintain.DIVI.get(), pMaintain.INBN.get());
      // Get Invoice Supplier's State
      if (APIBH.getSPYN().isBlank()) { 	
         found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, found_CIDMAS, currentCONO, APIBH.getSUNO());
      } else {
         found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, found_CIDMAS, currentCONO, APIBH.getSPYN());
      }
      // 1A) Search for VAT rate, with Division, Country and Area 	
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().move(APIBH.getDIVI());
      VATPC.setVTCD(pMaintain.VTCD.get());
      VATPC.setCSCD().move(APIBH.getBSCD());
      VATPC.setECAR().move(IDMAS.getECAR());
      VATPC.setFRDT(getIntMax(8));
      VATPC.SETLL("00", VATPC.getKey("00"));
      IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
      while (!IN93) {
         if (VATPC.getDIVI().EQ(APIBH.getDIVI()) && 	
             VATPC.getVTCD() == pMaintain.VTCD.get()  && 	
             VATPC.getCSCD().EQ(APIBH.getBSCD()) &&
             VATPC.getECAR().EQ(IDMAS.getECAR()) &&
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         }
         IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
      }

      // 1B) Search for VAT rate, with Division=blank, Country and Area 	
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().clear();
      VATPC.setVTCD(pMaintain.VTCD.get());
      VATPC.setCSCD().move(APIBH.getBSCD());
      VATPC.setECAR().move(IDMAS.getECAR());
      VATPC.setFRDT(getIntMax(8));	
      VATPC.SETLL("00", VATPC.getKey("00")); 	 	
      IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5)); 	 	
      while (!IN93) { 	 	
         if (VATPC.getVTCD() == pMaintain.VTCD.get() && 	
             VATPC.getCSCD().EQ(APIBH.getBSCD()) &&
             VATPC.getECAR().EQ(IDMAS.getECAR()) &&
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         } 	 	
         IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5)); 	 	
      } 		

      // 2A) Search for VAT rate, with Division, Country  and Area=blank		 
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().move(APIBH.getDIVI());
      VATPC.setVTCD(pMaintain.VTCD.get());
      VATPC.setCSCD().move(APIBH.getBSCD());
      VATPC.setECAR().clear();
      VATPC.setFRDT(getIntMax(8));
      VATPC.SETLL("00", VATPC.getKey("00"));
      IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
      while (!IN93) {
         if (VATPC.getDIVI().EQ(APIBH.getDIVI()) && 	
             VATPC.getVTCD() == pMaintain.VTCD.get() && 	
             VATPC.getCSCD().EQ(APIBH.getBSCD()) &&
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         } 
         IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
      }

      // 2B) Search for VAT rate, with Division=blank, Country  and Area=blank  	
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().clear();
      VATPC.setVTCD(pMaintain.VTCD.get());
      VATPC.setCSCD().move(APIBH.getBSCD());
      VATPC.setECAR().clear();
      VATPC.setFRDT(getIntMax(8));
      VATPC.SETLL("00", VATPC.getKey("00"));
      IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
      while (!IN93) {
         if (VATPC.getVTCD() == pMaintain.VTCD.get() &&
             VATPC.getCSCD().EQ(APIBH.getBSCD()) &&
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         }
         IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
      } 	

      // 3A) Search for VAT rate, with Division, Country=blank and Area=blank 	
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().move(APIBH.getDIVI());
      VATPC.setVTCD(pMaintain.VTCD.get());
      VATPC.setCSCD().clear();
      VATPC.setECAR().clear();
      VATPC.setFRDT(getIntMax(8));	
      VATPC.SETLL("00", VATPC.getKey("00"));
      IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
      while (!IN93) {
         if (VATPC.getDIVI().EQ(APIBH.getDIVI()) && 	
             VATPC.getVTCD() == pMaintain.VTCD.get() &&
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         }
         IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
      }

      // 3B) Search for VAT rate, with Division=blank, Country=blank  and Area=blank 	 	
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().clear();
      VATPC.setVTCD(pMaintain.VTCD.get());
      VATPC.setCSCD().clear();
      VATPC.setECAR().clear();
      VATPC.setFRDT(getIntMax(8));	
      VATPC.SETLL("00", VATPC.getKey("00"));
      IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
      while (!IN93) {
         if (VATPC.getVTCD() == pMaintain.VTCD.get() &&
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         }
         IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
      }
      return false;
   }

   /**
   * Retrieve VAT code. 	
   *    The VAT code is retrieved in the following sequence 	 	
   *    presented in the matrix below.
   * <PRE>
   *   *------------------------------------------------------------------* 	 	
   *   | Case | Division | Country | Area/State | VAT Rate 1 | VAT Rate 2 | 	
   *   | ---- | -------- | ------- | ---------- | ---------- | ---------- | 	 	
   *   | 1A   | DIVI     | CSCD    | ECAR       | VTP1   	 |	VTP2       |    
   *   | 1B   | blank    | CSCD    | ECAR       | VTP1	 	 | VTP2       |
   *   | 2A   | DIVI     | CSCD    | blank      | VTP1	 	 | VTP2       |
   *   | 2B   | blank    | CSCD    | blank      | VTP1 	    | VTP2       |
   *   | 3A   | DIVI     | blank   | blank      | VTP1	 	 | VTP2       |
   *   | 3B   | blank    | blank   | blank      | VTP1	 	 | VTP2       |
   *   *------------------------------------------------------------------* 	
   * </PRE>
   * @return 
   *    True if VAT code is found. The VAT code is found in CVATPC
   */
   public boolean retrieveVATCode() {
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, currentCONO, pMaintain.DIVI.get(), pMaintain.INBN.get());
      if (APIBH.getSPYN().isBlank()) { 	
         found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, found_CIDMAS, currentCONO, APIBH.getSUNO());
      } else {
         found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, found_CIDMAS, currentCONO, APIBH.getSPYN());
      }
      // 1A
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().moveLeftPad(APIBH.getDIVI());
      VATPCselection = setSelectionCVATPC_1();
      VATPC.SETLL("00", VATPC.getKey("00", 2));
      IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      while (!IN93) {
         if (VATPC.getDIVI().EQ(APIBH.getDIVI()) && 		
             VATPC.getCSCD().EQ(APIBH.getBSCD()) &&
             VATPC.getECAR().EQ(IDMAS.getECAR()) &&
             VATPC.getVTP1() == pMaintain.VTP1.get() && 
             VATPC.getVTP2() == pMaintain.VTP2.get()  && 
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         }
         IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      }
      // 1B
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().clear();
      VATPCselection = setSelectionCVATPC_1();
      VATPC.SETLL("00", VATPC.getKey("00", 2));
      IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      while (!IN93) {
         if (VATPC.getCSCD().EQ(APIBH.getBSCD()) &&
             VATPC.getECAR().EQ(IDMAS.getECAR()) &&
             VATPC.getVTP1() == pMaintain.VTP1.get() && 
             VATPC.getVTP2() == pMaintain.VTP2.get()  && 
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         }
         IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      }
      // 2A
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().moveLeftPad(APIBH.getDIVI());
      VATPCselection = setSelectionCVATPC_2();
      VATPC.SETLL("00", VATPC.getKey("00", 2));
      IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      while (!IN93) {
         if (VATPC.getDIVI().EQ(APIBH.getDIVI()) && 		
             VATPC.getCSCD().EQ(APIBH.getBSCD()) &&
             VATPC.getVTP1() == pMaintain.VTP1.get() && 
             VATPC.getVTP2() == pMaintain.VTP2.get()  && 
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         }
         IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      }
      // 2B
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().clear();
      VATPCselection = setSelectionCVATPC_2();
      VATPC.SETLL("00", VATPC.getKey("00", 2));
      IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      while (!IN93) {
         if (VATPC.getCSCD().EQ(APIBH.getBSCD()) &&
             VATPC.getVTP1() == pMaintain.VTP1.get() && 
             VATPC.getVTP2() == pMaintain.VTP2.get()  && 
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         }
         IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      }
      // 3A
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().moveLeftPad(APIBH.getDIVI());
      VATPCselection = setSelectionCVATPC_3();
      VATPC.SETLL("00", VATPC.getKey("00", 2));
      IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      while (!IN93) {
         if (VATPC.getDIVI().EQ(APIBH.getDIVI()) && 
             VATPC.getVTP1() == pMaintain.VTP1.get() && 
             VATPC.getVTP2() == pMaintain.VTP2.get()  && 
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         }
         IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      }
      // 3B
      VATPC.setCONO(currentCONO);
      VATPC.setDIVI().clear();
      VATPCselection = setSelectionCVATPC_3();
      VATPC.SETLL("00", VATPC.getKey("00", 2));
      IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      while (!IN93) {
         if (VATPC.getVTP1() == pMaintain.VTP1.get() && 
             VATPC.getVTP2() == pMaintain.VTP2.get()  && 
             VATPC.getFRDT() <= APIBH.getIVDT()) {
            return true;
         }
         IN93 = !VATPC.READE("00", VATPC.getKey("00", 2), VATPCselection);
      }
      return false;
   }

  /**
   * Retrieve Cost Element based on the given charge text
   * @return true costing element is found 
   */
   public boolean retrieveCostingElement() {
      int countInvLines = 0;
      if (pMaintain.CHGT.get().isBlank()) {
         return false;
      }
      PCELE.setCONO(currentCONO);
      PCELE.setEXTY(2);
      PCELE.setDIMT(1);
      PCELE.SETLL_SCAN("20", PCELE.getKey("20", 3)); 	 	
      PCELE.setSelection("20", "INTX30", "EQ", String.valueOf(pMaintain.CHGT.get())); 	 	
      IN91 = !PCELE.READE("20", PCELE.getKey("20", 3));
      if (IN91) {
         PCELE.setDIMT(3);
         PCELE.SETLL_SCAN("20", PCELE.getKey("20", 3)); 	 	
         PCELE.setSelection("20", "INTX30", "EQ", String.valueOf(pMaintain.CHGT.get())); 	 	
         IN91 = !PCELE.READE("20", PCELE.getKey("20", 3));
      }
      if (IN91) {
         PCELE.SETLL_SCAN("00", PCELE.getKey("00", 1)); 	 	
         PCELE.setSelection("00", "INTX30", "EQ", String.valueOf(pMaintain.CHGT.get())); 
         IN91 = !PCELE.READE("00", PCELE.getKey("00", 1));
      }   
      if (IN91) {
         if (!pMaintain.partialVld.get()) {
            // MSGID=X_00106 Costing element not found, using charge text
            pMaintain.messages.addError(this.DSPGM, "CEID", "X_00106");
         }
         return false;
      } else {
         POEXP.setCONO(currentCONO);
         POEXP.setPUNO().move(pMaintain.PUNO.get());
         POEXP.setPNLI(pMaintain.PNLI.get());
         POEXP.setPNLS(pMaintain.PNLS.get());
         POEXP.setCEID().move(PCELE.getCEID());
         if (!POEXP.CHAIN("30", POEXP.getKey("30", 5))) {
            if (!pMaintain.partialVld.get()) {
               // MSGID=X_00107 Costing element &1 is not used for this line
               pMaintain.messages.addError(this.DSPGM, "CEID", "X_00107", PCELE.getCEID());
            }
            return false;
         } else {
            // Check FAPIBL if multiple line charge exist having the same sequence number
            APIBL.setCONO(currentCONO);
            APIBL.setDIVI().move(pMaintain.DIVI.get());
            APIBL.setREPN(pMaintain.REPN.get());
            APIBL.setPUNO().move(pMaintain.PUNO.get());
            APIBL.setPNLI(pMaintain.PNLI.get());
            APIBL.setPNLS(pMaintain.PNLS.get());
            APIBL.setCDSE(pMaintain.CDSE.get());
            APIBL.SETLL("40", APIBL.getKey("40", 7));
            while (APIBL.READE("40", APIBL.getKey("40", 7))) { 
               if (APIBL.getRDTP() == cRefRDTPext.LINE_CHARGE()) {
                  countInvLines++;
               }
            }
            pMaintain.CEID.set().move(POEXP.getCEID());
            pMaintain.CDSE.set(POEXP.getCDSE());
            if (countInvLines >= 1) {
               if (!pMaintain.partialVld.get()) {
                  //   MSGID=WCD1604 Sequence number - costing element &1 already exists
                  pMaintain.messages.addError(this.DSPGM, "CEID", "WCD1604", PCELE.getCEID());
               }
               return false;
            }
            return true;
         }
      }
   }

   /**
   * Sets Invoice batch number to status - Update in progress
   * @param DIVI
   *    Division
   * @param INBN
   *    Supplier invoice batch number
   * @param messages
   *    Container with list of messages
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @param currentCHNO
   *    Current change number. Used for checking if the record has been updated by another user.
   *    Send -1 if no such check should be performed.
   * @return
   *    True if the invoice was locked.
   */
   public boolean lockInvoiceForUpdate(MvxString DIVI, long INBN, cCRMessageList messages, MvxString alreadyLockedByJob, int currentCHNO) {
      boolean error = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // =======================================================
      // Set Invoice batch number to status - Update in progress
      // =======================================================
      pAPS450Fnc_lockForUpdate = get_pAPS450Fnc_lockForUpdate();
      pAPS450Fnc_lockForUpdate.messages.forgetNotifications();
      pAPS450Fnc_lockForUpdate.prepare();
      // Set input parameters
      // - Division
      pAPS450Fnc_lockForUpdate.DIVI.set().moveLeftPad(DIVI);
      // - Invoice batch number
      pAPS450Fnc_lockForUpdate.INBN.set(INBN);
      // - Change number
      pAPS450Fnc_lockForUpdate.CHNO.set(currentCHNO);
      // =========================================
      apCall("APS450Fnc", pAPS450Fnc_lockForUpdate);
      // =========================================
      // Handle messages
      if (pAPS450Fnc_lockForUpdate.messages.existError()) {
         error = true;
         // Return error messages
         messages.addAllErrors(pAPS450Fnc_lockForUpdate.messages);
      }
      alreadyLockedByJob.moveLeft(toChar(pAPS450Fnc_lockForUpdate.alreadyLck.get()));
      // Release resources allocated by the parameter list.
      pAPS450Fnc_lockForUpdate.release();
      return !error;
   }

   /**
   * Sets Invoice batch number to status - Update in progress
   *    Company
   * @param DIVI
   *    Division
   * @param INBN
   *    Supplier invoice batch number
   * @param messages
   *    Container with list of messages
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @return
   *    True if the invoice was locked.
   */
   public boolean lockInvoiceForAdjustLine(MvxString DIVI, long INBN, cCRMessageList messages, MvxString alreadyLockedByJob) {
      boolean error = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // =======================================================
      // Set Invoice batch number to status - Update in progress
      // =======================================================
      pAPS450Fnc_lockForAdjLine = get_pAPS450Fnc_lockForAdjLine();
      pAPS450Fnc_lockForAdjLine.messages.forgetNotifications();
      pAPS450Fnc_lockForAdjLine.prepare();
      // Set input parameters
      // - Division
      pAPS450Fnc_lockForAdjLine.DIVI.set().moveLeftPad(DIVI);
      // - Invoice batch number
      pAPS450Fnc_lockForAdjLine.INBN.set(INBN);
      // =========================================
      apCall("APS450Fnc", pAPS450Fnc_lockForAdjLine);
      // =========================================
      // Handle messages
      if (pAPS450Fnc_lockForAdjLine.messages.existError()) {
         error = true;
         // Return error messages
         messages.addAllErrors(pAPS450Fnc_lockForAdjLine.messages);
      }
      alreadyLockedByJob.moveLeft(toChar(pAPS450Fnc_lockForAdjLine.alreadyLck.get()));
      // Release resources allocated by the parameter list.
      pAPS450Fnc_lockForAdjLine.release();
      return !error;
   }

   /**
   * Sets Invoice batch number to status - Unlocked
   *    Company
   * @param DIVI
   *    Division
   * @param INBN
   *    Supplier invoice batch number
   */
   public void unlockInvoice(MvxString DIVI, long INBN) {
      // =============================================
      // Set Invoice batch number to status - Unlocked
      // =============================================
      pAPS450Fnc_unlock = get_pAPS450Fnc_unlock();
      pAPS450Fnc_unlock.messages.forgetNotifications();
      pAPS450Fnc_unlock.prepare();
      // Set input parameters
      // - Division
      pAPS450Fnc_unlock.DIVI.set().moveLeftPad(DIVI);
      // - Invoice batch number
      pAPS450Fnc_unlock.INBN.set(INBN);
      // =========================================
      apCall("APS450Fnc", pAPS450Fnc_unlock);
      // =========================================
      // Release resources allocated by the parameter list.
      pAPS450Fnc_unlock.release();
   }

   /**
   * Checks if the invoice is work in progress. Any error messages
   * are returned in the messages parameter.
   * @param DIVI
   *    Division
   * @param INBN
   *    Supplier invoice batch number
   * @param messages
   *    Container with list of messages
   */
   public void checkInvoiceForWIP(MvxString DIVI, long INBN, cCRMessageList messages) {
      // =================================================
      // Check if Invoice batch number is Work in progress
      // =================================================
      pAPS450Fnc_checkIfWIP = get_pAPS450Fnc_checkIfWIP();
      pAPS450Fnc_checkIfWIP.messages.forgetNotifications();
      pAPS450Fnc_checkIfWIP.prepare();
      // Set input parameters
      // - Division
      pAPS450Fnc_checkIfWIP.DIVI.set().moveLeftPad(DIVI);
      // - Invoice batch number
      pAPS450Fnc_checkIfWIP.INBN.set(INBN);
      // =========================================
      apCall("APS450Fnc", pAPS450Fnc_checkIfWIP);
      // =========================================
      // Handle messages
      if (pAPS450Fnc_checkIfWIP.messages.existError()) {
         // Return error messages
         messages.addAllErrors(pAPS450Fnc_checkIfWIP.messages);
      }
      // Release resources allocated by the parameter list.
      pAPS450Fnc_checkIfWIP.release();
   }

   /**
   * Finds MPLINE record
   * @return
   *    true if MPLINE record is found
   */
   public boolean findMPLINERecord() {
      PLINE.setCONO(currentCONO);
      PLINE.setPUNO().move(pMaintain.PUNO.get());
      PLINE.setPNLI(pMaintain.PNLI.get());
      PLINE.setPNLS(pMaintain.PNLS.get());
      found_MPLINE = PLINE.CHAIN("00", PLINE.getKey("00", 4));
      if (!found_MPLINE) {
         // No valid record found
         PLINE.clearNOKEY("00");
      }
      return found_MPLINE;
   }

   /**
   * Retrieve AP settings from APS905 
   */
   public void retrieveAPSettings() {
      // - Chain APS905 - Settings for AP
      found_CMNCMP = cRefCONOext.getCMNCMP(MNCMP, found_CMNCMP, currentCONO);
      found_CSYPAR_APS905 = cRefPGNMext.getCSYPAR_PGNM_DIVIorBlank(SYPAR, found_CSYPAR_APS905, MNCMP.getCMTP(), currentCONO, pMaintain.DIVI.get(), "APS905");
      APS905DS.setAPS905DS().moveLeft(SYPAR.getPARM());
   }      

  /**
   *  Set selection for CVATPC (CVCSCD, CVECAR, CVVTP1, CVVTP2)
   */
   public FieldSelection setSelectionCVATPC_1() { 	
      VATPCselection = null; 	
      Expression exp = Expression.createEQ("CVATPC", "CVCSCD", String.valueOf(APIBH.getBSCD()));
      exp = exp.AND(Expression.createEQ("CVATPC", "CVECAR", String.valueOf(IDMAS.getECAR())));
      exp = exp.AND(Expression.createEQ("CVATPC", "CVVTP1", String.valueOf(pMaintain.VTP1.get())));
      exp = exp.AND(Expression.createEQ("CVATPC", "CVVTP2", String.valueOf(pMaintain.VTP2.get())));
      FieldSelection fs = new FieldSelection("CVATPC", "00"); 	
      fs.setExpression(exp); 	
      return fs; 	
   }

  /**
   *  Set selection for CVATPC (CVCSCD, "   ", CVVTP1, CVVTP2)
   */
   public FieldSelection setSelectionCVATPC_2() { 	
      VATPCselection = null; 	
      Expression exp = Expression.createEQ("CVATPC", "CVCSCD", String.valueOf(APIBH.getBSCD()));
      exp = exp.AND(Expression.createEQ("CVATPC", "CVECAR", String.valueOf(" ")));
      exp = exp.AND(Expression.createEQ("CVATPC", "CVVTP1", String.valueOf(pMaintain.VTP1.get())));
      exp = exp.AND(Expression.createEQ("CVATPC", "CVVTP2", String.valueOf(pMaintain.VTP2.get())));
      FieldSelection fs = new FieldSelection("CVATPC", "00"); 	
      fs.setExpression(exp); 	
      return fs; 	
   }

  /**
   *  Set selection for CVATPC ("   ", "   ", CVVTP1, CVVTP2)
   */
   public FieldSelection setSelectionCVATPC_3() { 	
      VATPCselection = null; 	
      Expression exp = Expression.createEQ("CVATPC", "CVCSCD", String.valueOf(" "));
      exp = exp.AND(Expression.createEQ("CVATPC", "CVECAR", String.valueOf(" ")));
      exp = exp.AND(Expression.createEQ("CVATPC", "CVVTP1", String.valueOf(pMaintain.VTP1.get())));
      exp = exp.AND(Expression.createEQ("CVATPC", "CVVTP2", String.valueOf(pMaintain.VTP2.get())));
      FieldSelection fs = new FieldSelection("CVATPC", "00"); 	
      fs.setExpression(exp); 	
      return fs; 	
   }

   /**
   * Initiation of function program
   */
   public void INIT() { 	
      this.PXCONO = currentCONO;
      this.PXDIVI.clear();
      // Create record
      if (FAPIBL_record == null) {
         FAPIBL_record = APIBL.getEmptyRecord();
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
   public mvx.db.dta.CSYTAB SYTAB;
   public mvx.db.dta.CMNDIV MNDIV;
   public mvx.db.dta.CSYPAR SYPAR;
   public mvx.db.dta.MPHEAD PHEAD;
   public mvx.db.dta.MITAUN ITAUN;
   public mvx.db.dta.MITVEN ITVEN;
   public mvx.db.dta.MPOEXP POEXP;
   public mvx.db.dta.MITMAS ITMAS;
   public mvx.db.dta.CFACIL FACIL;
   public mvx.db.dta.MPLINE PLINE;
   public mvx.db.dta.MPCELE PCELE;
   public mvx.db.dta.CMNCMP MNCMP;
   public mvx.db.dta.CVATPC VATPC;
   public mvx.db.dta.CIDMAS IDMAS;
   public mvx.db.dta.CIDVEN IDVEN;
   public mvx.db.dta.CSUDIV SUDIV;
   public mvx.db.dta.MPCLAH PCLAH;
   public mvx.db.dta.MPCLAL PCLAL;

   public void initMDB() {
      SYSTR = (mvx.db.dta.CSYSTR)getMDB("CSYSTR", SYSTR);
      APIBH = (mvx.db.dta.FAPIBH)getMDB("FAPIBH", APIBH);
      APIBL = (mvx.db.dta.FAPIBL)getMDB("FAPIBL", APIBL);
      SYTAB = (mvx.db.dta.CSYTAB)getMDB("CSYTAB", SYTAB);
      MNDIV = (mvx.db.dta.CMNDIV)getMDB("CMNDIV", MNDIV);
      SYPAR = (mvx.db.dta.CSYPAR)getMDB("CSYPAR", SYPAR);
      PHEAD = (mvx.db.dta.MPHEAD)getMDB("MPHEAD", PHEAD);
      ITAUN = (mvx.db.dta.MITAUN)getMDB("MITAUN", ITAUN);
      POEXP = (mvx.db.dta.MPOEXP)getMDB("MPOEXP", POEXP);
      ITMAS = (mvx.db.dta.MITMAS)getMDB("MITMAS", ITMAS);
      ITVEN = (mvx.db.dta.MITVEN)getMDB("MITVEN", ITVEN);
      FACIL = (mvx.db.dta.CFACIL)getMDB("CFACIL", FACIL);
      PLINE = (mvx.db.dta.MPLINE)getMDB("MPLINE", PLINE);
      PCELE = (mvx.db.dta.MPCELE)getMDB("MPCELE", PCELE);
      MNCMP = (mvx.db.dta.CMNCMP)getMDB("CMNCMP", MNCMP);
      VATPC = (mvx.db.dta.CVATPC)getMDB("CVATPC", VATPC);
      IDMAS = (mvx.db.dta.CIDMAS)getMDB("CIDMAS", IDMAS);
      IDVEN = (mvx.db.dta.CIDVEN)getMDB("CIDVEN", IDVEN);
      SUDIV = (mvx.db.dta.CSUDIV)getMDB("CSUDIV", SUDIV);
      PCLAH = (mvx.db.dta.MPCLAH)getMDB("MPCLAH", PCLAH);
      PCLAL = (mvx.db.dta.MPCLAL)getMDB("MPCLAL", PCLAL);
   }

   // Entry parameters
   @ParameterList
   public cPXAPS451FncINmaintain pMaintain = null;
   @ParameterList
   public cPXAPS451FncINdelete pDelete = null;
   @ParameterList
   public cPXAPS451FncINcopy pCopy = null;
   @ParameterList
   public cPXAPS451FncINsettings pSettings = null;
   @ParameterList
   public cPXAPS451FncINadjustLine pAdjustLine = null;

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPcheckIfWIP get_pAPS450Fnc_checkIfWIP() {
      if (pAPS450Fnc_checkIfWIP == null) {
         cPXAPS450FncOPcheckIfWIP newPlist = new cPXAPS450FncOPcheckIfWIP();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_checkIfWIP.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_checkIfWIP;
      }
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPlockForUpdate get_pAPS450Fnc_lockForUpdate() {
      if (pAPS450Fnc_lockForUpdate == null) {
         cPXAPS450FncOPlockForUpdate newPlist = new cPXAPS450FncOPlockForUpdate();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_lockForUpdate.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_lockForUpdate;
      }
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPlockForAdjLine get_pAPS450Fnc_lockForAdjLine() {
      if (pAPS450Fnc_lockForAdjLine == null) {
         cPXAPS450FncOPlockForAdjLine newPlist = new cPXAPS450FncOPlockForAdjLine();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_lockForAdjLine.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_lockForAdjLine;
      }
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPunlock get_pAPS450Fnc_unlock() {
      if (pAPS450Fnc_unlock == null) {
         cPXAPS450FncOPunlock newPlist = new cPXAPS450FncOPunlock();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_unlock.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_unlock;
      }
   }

   public MvxRecord FAPIBL_record = null;
   public cPXCRS98X PXCRS98X = new cPXCRS98X(this);
   public cCRCommon CRCommon = new cCRCommon(this);
   public cCRCalendar CRCalendar = new cCRCalendar(this);
   public cPXRTVIMG PXRTVIMG = new cPXRTVIMG(this);
   public cPLCRTVT PLCRTVT = new cPLCRTVT(this);
   public cPXAPS450FncOPcheckIfWIP pAPS450Fnc_checkIfWIP = null;
   public cPXAPS450FncOPlockForUpdate pAPS450Fnc_lockForUpdate = null;
   public cPXAPS450FncOPlockForAdjLine pAPS450Fnc_lockForAdjLine = null;
   public cPXAPS450FncOPunlock pAPS450Fnc_unlock = null;

   public sDSCUCD DSCUCD = new sDSCUCD(this);
   public sAPIDS APIDS = new sAPIDS(this);
   public FieldSelection VATPCselection; 	
   public boolean found_CSYSTR;
   public boolean found_CMNDIV;
   public boolean found_MPHEAD;
   public boolean found_CSYTAB_CUCD;
   public boolean found_CSYTAB_VTCD;
   public boolean found_CSYTAB_SERS;
   public boolean found_CSYTAB_PUUN;
   public boolean found_CSYTAB_PPUN;
   public boolean found_FAPIBH;
   public boolean found_MITAUN;
   public boolean found_MPOEXP;
   public boolean found_MITMAS;
   public boolean found_MPLINE;
   public boolean found_CFACIL;
   public boolean found_MPCELE;
   public boolean found_CMNCMP;
   public boolean found_CIDMAS;
   public boolean found_CIDVEN;
   public boolean found_CSUDIV;
   public boolean found_MPCLAH;
   public boolean found_MPCLAL;
   public MvxString alreadyLockedByJob = new MvxString(1);
   public int nextTRNO;
   public double savedVTA1;
   public double savedVTA2;
   public MvxString foundParam_CIDVEN = new MvxString(1);
   public MvxString foundParam_CSUDIV = new MvxString(1);

   public MvxStruct rXXPAR1 = new MvxStruct(100);
   public MvxString XXPAR1 = rXXPAR1.newString(0, 100);
   public MvxString CSSPIC = rXXPAR1.newChar(0);
   public MvxString CSDSEQ = rXXPAR1.newString(cRefSPIC.length(), cRefPSEQ.length());
   public MvxString CSQTTP = rXXPAR1.newInt(cRefSPIC.length() + cRefPSEQ.length(), cRefQTTP.length());
   public MvxString CSINBN = rXXPAR1.newLong(cRefSPIC.length() + cRefPSEQ.length() + cRefQTTP.length(), cRefINBN.length());

   public MvxStruct rDSKFLD = new MvxStruct(cRefINBN.length() + cRefTRNO.length());
   public MvxString DSKFLD = rDSKFLD.newString(0, cRefINBN.length() + cRefTRNO.length());
   public MvxString DSINBN = rDSKFLD.newLong(0, cRefINBN.length());
   public MvxString DSTRNO = rDSKFLD.newInt(cRefINBN.length(), cRefTRNO.length());

   public MvxStruct rMessageData_X_00051 = new MvxStruct(40 + cRefOPT2.length() + cRefSUPA.length());
   public MvxString messageData_X_00051 = rMessageData_X_00051.newString(0, 40 + cRefOPT2.length() + cRefSUPA.length());
   public MvxString messageData_X_00051_TEXT = rMessageData_X_00051.newString(0, 40);
   public MvxString messageData_X_00051_OPT2 = rMessageData_X_00051.newString(40, cRefOPT2.length());
   public MvxString messageData_X_00051_SUPA = rMessageData_X_00051.newString(40 + cRefOPT2.length(), cRefSUPA.length());

   public MvxStruct rMessageData_X_01011 = new MvxStruct(cRefCDSE.length() + cRefCEID.length());
   public MvxString messageData_X_01011 = rMessageData_X_01011.newString(0, cRefCDSE.length() + cRefCEID.length());
   public MvxString messageData_X_01011_CDSE = rMessageData_X_01011.newInt(0, cRefCDSE.length());
   public MvxString messageData_X_01011_CEID = rMessageData_X_01011.newString(cRefCDSE.length(), cRefCEID.length());

   public MvxStruct rMessageData_AP10046 = new MvxStruct(cRefSUNO.length() + cRefPUNO.length());
   public MvxString messageData_AP10046 = rMessageData_AP10046.newString(0, cRefSUNO.length() + cRefPUNO.length());
   public MvxString messageData_AP10046_SUNO = rMessageData_AP10046.newString(0, cRefSUNO.length());
   public MvxString messageData_AP10046_PUNO = rMessageData_AP10046.newString(cRefSUNO.length(), cRefPUNO.length());

   public MvxStruct rMessageData_AP05306 = new MvxStruct(cRefPUUN.length() + cRefPUUN.length());
   public MvxString messageData_AP05306 = rMessageData_AP05306.newString(0, cRefPUUN.length() + cRefPUUN.length());
   public MvxString messageData_AP05306_PUUN_1 = rMessageData_AP05306.newString(0, cRefPUUN.length());
   public MvxString messageData_AP05306_PUUN_2 = rMessageData_AP05306.newString(cRefPUUN.length(), cRefPUUN.length());

   public MvxStruct rMessageData_AP45103 = new MvxStruct(cRefSUNO.length() + cRefCLAN.length());
   public MvxString messageData_AP45103 = rMessageData_AP45103.newString(0, cRefSUNO.length() + cRefCLAN.length());
   public MvxString messageData_AP45103_SUNO = rMessageData_AP45103.newString(0, cRefSUNO.length());
   public MvxString messageData_AP45103_CLAN = rMessageData_AP45103.newString(cRefSUNO.length(), cRefCLAN.length());  

   public MvxStruct rMessageData_AP_0365 = new MvxStruct(cRefOPT2.length() + 1);
   public MvxString messageData_AP_0365 = rMessageData_AP_0365.newString(0, cRefOPT2.length() + 1);
   public MvxString messageData_AP_0365_OPT2 = rMessageData_AP_0365.newString(0, cRefOPT2.length());
   public MvxString messageData_AP_0365_RDTP = rMessageData_AP_0365.newInt(cRefOPT2.length(), 1);

   public sAPS905DS APS905DS = new sAPS905DS(this);
   public boolean found_CSYPAR_APS905;
   public cPLCHKAD PLCHKAD = new cPLCHKAD(this);

   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(SYSTR);
      v.addElement(APIBH);
      v.addElement(APIBL);
      v.addElement(SYTAB);
      v.addElement(SYPAR);
      v.addElement(MNDIV);
      v.addElement(PHEAD);
      v.addElement(ITAUN);
      v.addElement(ITVEN);
      v.addElement(POEXP);
      v.addElement(ITMAS);
      v.addElement(FACIL);
      v.addElement(PLINE);
      v.addElement(PCELE);
      v.addElement(MNCMP);
      v.addElement(VATPC);
      v.addElement(IDMAS);
      v.addElement(IDVEN);
      v.addElement(SUDIV);
      v.addElement(PCLAH);
      v.addElement(PCLAL);
      v.addElement(pMaintain);
      v.addElement(pDelete);
      v.addElement(pCopy);
      v.addElement(pSettings);
      v.addElement(pAdjustLine);
      v.addElement(pAPS450Fnc_checkIfWIP);
      v.addElement(pAPS450Fnc_lockForUpdate);
      v.addElement(pAPS450Fnc_lockForAdjLine);
      v.addElement(pAPS450Fnc_unlock);
      v.addElement(foundParam_CIDVEN);
      v.addElement(foundParam_CSUDIV);
      v.addElement(VATPCselection);
      v.addElement(PXCRS98X);
      v.addElement(CRCommon);
      v.addElement(CRCalendar);
      v.addElement(PXRTVIMG);
      v.addElement(PLCRTVT);
      v.addElement(rXXPAR1);
      v.addElement(DSCUCD);
      v.addElement(APIDS);
      v.addElement(alreadyLockedByJob);
      v.addElement(rDSKFLD);
      v.addElement(rMessageData_X_00051);
      v.addElement(rMessageData_X_01011);
      v.addElement(rMessageData_AP10046);
      v.addElement(rMessageData_AP05306);
      v.addElement(rMessageData_AP45103);
      v.addElement(rMessageData_AP_0365);
      v.addElement(APS905DS);
      v.addElement(PLCHKAD);
      return version;
   }

   public void clearInstance() {
      super.clearInstance();
      nextTRNO = 0;
      found_CSYSTR = false;
      found_CMNDIV = false;
      found_MPHEAD = false;
      found_CSYTAB_CUCD = false;
      found_CSYTAB_VTCD = false;
      found_CSYTAB_SERS = false;
      found_CSYTAB_PUUN = false;
      found_CSYTAB_PPUN = false;
      found_FAPIBH = false;
      found_MITAUN = false;
      found_MPOEXP = false;
      found_MITMAS = false;
      found_CFACIL = false;
      found_MPLINE = false;
      found_MPCELE = false;
      found_CMNCMP = false;
      found_CIDMAS = false;
      found_CIDVEN = false;
      found_CSUDIV = false;
      found_MPCLAH = false;
      found_MPCLAL = false;
      savedVTA1 = 0d;
      savedVTA2 = 0d;
      found_CSYPAR_APS905 = false;
   }

   public final static cLanguageConst VAT_REC_AMOUNT_CAPTION = cLanguageConst.fieldHeading("WVT35");

   public final static cLanguageConst GROSS_AMOUNT_CAPTION = cLanguageConst.fieldHeading("WGLA0");

   public final static String PANELS_REQUIRED_FOR_ADD = "E";

   public final static String ALLOWED_PANELS = PANELS_REQUIRED_FOR_ADD + "T-";

   public final static String DEFAULT_PANELS = PANELS_REQUIRED_FOR_ADD + "T";

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

public final static String _spNumber="MAK_16494_140605_09:18";

public final static String _GUID="885CD405711549b29C607330FDBDCC4A";

public final static String _tempFixComment="";

public final static String _build="000000000000351";

public final static String _pgmName="APS451Fnc";

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
   } // end of method [][] getStandardModification()

   public final static String [][] _standardModifications={
      {"JT-556359","140318","11893","Not possible to use Alias number in APS451"},
      {"JT-595301","140516","16494","Use of different units of measure (qty and price) doesn't work in APS451"},
      {"JT-604224","140606","16494","Use of different units of measure (qty and price) doesn't work in APS451"}
   };
}