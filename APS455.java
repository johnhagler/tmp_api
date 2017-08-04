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
import mvx.runtime.*;
import mvx.db.dta.*;
import mvx.app.util.*;
import mvx.app.plist.*;
import mvx.app.ds.*;
import mvx.dsp.common.GenericDSP;
import mvx.dsp.obj.*;
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
* This interactive program lets you select supplier invoices to perform an operation
* on. The selected supplier invoices are submitted in a batch job. 
*/
public class APS455 extends Interactive 
{

   /**
   * B-panel - initiate.
   */
   public void PBINZ() {
      ensureReadOfTables();
      // Forget notifications
      if (pMaintain != null) {
         pMaintain.messages.forgetNotifications();
      }
      // Set last panel
      lastPanel = 'B';
      // Position cursor
      if (!isBookmarkProcessing()) {
         if (IN61) {
            DSP.setFocus("W1LIVR");
         }
      }
      // Init of subfile information
      if (IN61) {
         XJLIVR.move(DSP.W1LIVR);
      }
      XLLIVR.clear();
      this.WSRGTM = movexDateTime();
      DSP.XBRRNA = 0;
      DSP.XBRRNM = 0;
      if (isBookmarkProcessing()) {
         // Do not fill the subfile at this stage
         return;
      }
      // Clear the subfile
      IN94 = true;
      IN95 = false;
      DSP.clearSFL("BC");
      IN94 = false;
      IN95 = false;
      IN96 = false;
      // Set limit & build subfile, Sorting order 1
      if (IN61) {
         SYSTP.setLIVR().move(DSP.W1LIVR);
         SYSTP.SETLL_SCAN("00", SYSTP.getKey("00"));
      }
      PBINZ_SETLL(); // exit point for modification
      PBROL();
   }

   /**
   * B-panel - display - initiate.
   */
   public void PBDSP_INZ() {
      // Set last panel
      lastPanel = 'B';
   }

   /**
   * B-panel - check.
   */
   public void PBCHK() {
      ensureReadOfTables();
      DSP.WBRRNA = 0;
      PBCHK_process();
      PBCHK_ExecuteOption(); // exit point for modification
      // Clear option in case of error
      if (DSP.WBRRNA != 0 && IN60) {
         DSP.clearOption();
         DSP.XBRRNA = DSP.WBRRNA;
         DSP.updateSFL("BS");
      }
   }

   /**
   * B-panel - check - validate and process user action.
   *
   * Turns on IN60 if an error was found.
   */
   public void PBCHK_process() {
      // Declaration
      updateSelection = false;
      submitSelection = false;
      setACDTtoCurrentDate = true;
      // Check sorting order
      if (PBCHK_ValidateInquiry()) {  // exit point for modification
         if (IN60) {
            return;
         }
      } else {
         if (!validSortingOrder(DSP.WBQTTP)) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WBQTTP");
            // MSGID=WQT0101 Sorting order &1 is invalid
            COMPMQ("WQT0101", formatToString(DSP.WBQTTP, 2));
            return;
         }
      }
      // Set sorting order
      if (!getIN(60 + DSP.WBQTTP)) {
         setIndicatorForSortingOrder(DSP.WBQTTP);
         picSetMethod('I');
         return;
      }
      // Check panel sequence
      if (!seqCheckSeq(getAllowedPanels(), DSP.WWPSEQ)) {
         return;
      }
      this.CSSQ.moveLeft(this.SEQ);
      // Check list head fields
      // - No field to check
      // Get option and set primary keys
      if (isBookmarkProcessing()) {
         setKeyFieldsInPBCHKFromBookmark();
         if (option9FromBookmark) {
            DSP.WSOPT2.move(" 9");
         }
      } else {
         // Option and keys from subfile head
         DSP.WSOPT2.clear();
         SYSTP.setLIVR().clear();
         if (IN61) {
            DSP.WSOPT2.move(DSP.WBOPT2);
            SYSTP.setLIVR().move(DSP.W1LIVR);
         }
      }
      // - option and keys from subfile
      if (DSP.WSOPT2.isBlank() && DSP.XBRRNM != 0) {
         boolean endOfSfl = false;
         do {
            DSP.WBRRNA = 0;
            endOfSfl = DSP.readSFL("BS");
            if (!endOfSfl) {
               SYSTP.setLIVR().move(DSP.WSLIVR);
            }
         } while (DSP.WSOPT2.isBlank() && !endOfSfl);
      }
      if (pMaintain != null) {
         pMaintain.messages.forgetNotifications();
      }
      // New start position
      if (DSP.WSOPT2.isBlank()) {
         if (IN61) {
            if (DSP.W1LIVR.NE(XJLIVR)) {
               picSetMethod('I');
               return;
            }
         }
         PBCHK_CheckStartPosition(); // exit point for modification
      }
      // No option given
      if (DSP.WSOPT2.isBlank()) {
         picSetMethod('D');
         return;
      }
      // Check option - adjust option and check if authorized
      XXOPT2.clear();
      if (!DSP.WSOPT2.isBlank()) {
         this.XXOPT2.move(DSP.WSOPT2);
         if (PxCHKoption()) {
            return;
         }
      }
      // Reset picture mode indicators IN41 - IN49
      CRCommon.clearMode();
      // Set panel sequence
      seqLoadSavedSeq();
      // Transfer data to calling program
      if (this.XXOPT2.EQ(" 1") && DSP.WBRRNA > 0 && cCRCommon.checkIfTransferData(this)) {
         Bookmark bookmark = getBookmark();
         bookmark.setRecord("CSYSTP", getPrimaryKeyForTable("CSYSTP"));
         bookmark = null;
         LDAZZ.TPGM.move(this.DSPGM);
         picPush("**");
         return;
      }
      // Check if valid option
      if (!DSP.WSOPT2.isBlank() && !PBCHK_ValidateOption()) { // exit point for modification
         if (this.XXOPT2.LT(" 1")  ||
             this.XXOPT2.GT(" 5") &&
             this.XXOPT2.NE(" 9"))
         {
            picSetMethod('D');
            IN60 = true;
            if (DSP.WBRRNA == 0) {
               DSP.setFocus("WBOPT2");
            }
            // MSGID=WOP0101 Option &1 is invalid
            COMPMQ("WOP0101", this.XXOPT2);
            return;
         }
      }
      // Check if option (1-9) is authorized
      this.XI = toInt(this.XXOP22.getChar());
      if (this.XI >= 1 && this.XI <= 9 && this.XXOP21.getChar() == ' ' && !toBoolean(this.PXO.charAt(this.XI - 1)) )
      {
         picSetMethod('D');
         IN60 = true;
         if (DSP.WBRRNA == 0) {
            DSP.setFocus("WBOPT2");
         }
         // MSGID=XAU0001 You do not have authorization for the selected option
         COMPMQ("XAU0001");
         return;
      }
      // Check key values
      if (SYSTP.getLIVR().isBlank()) {
         picSetMethod('D');
         IN60 = true;
         if (IN61) {
            DSP.setFocus("W1LIVR");
         }
         // MSGID=WLI1102 Report version must be entered
         COMPMQ("WLI1102");
         return;
      }
      // Check record
      found_CSYSTP = SYSTP.CHAIN("00", SYSTP.getKey("00"));
      passCSYSTP = found_CSYSTP;
      IN91 = !found_CSYSTP;
      if (isBookmarkProcessing()) { 	
         validateRecordInPBCHKInCaseOfBookmark(); 	
         if (IN60) { 	
            return;
         } 	
      } 	
      // Add
      if (seqAddModeOk(getPanelsRequiredForAdd())) {
         if (CRCommon.getMode() == cEnumMode.ADD) {
            CRCommon.addPanelsFromSelectedPanelSequence();
            updateSelection = true;
            submitSelection = false;
            return;
         }
      } else {
         if (DSP.WBRRNA == 0) {
            if (IN61) {
               DSP.setFocus("W1LIVR");
            }
         }
         // MSGID=WLI1104 Report version &1 already exists
         COMPMQ("WLI1104", SYSTP.getLIVR());
         return;
      }
      // Check if exist
      if (!found_CSYSTP) {
         picSetMethod('D');
         IN60 = true;
         // MSGID=WLI1103 Report version &1 does not exist
         COMPMQ("WLI1103", SYSTP.getLIVR());
         return;
      }
      // Change / Copy / Delete / Display
      if (!seqChangeModeOk() ||
          !seqCopyModeOk() ||
          !seqDeleteModeOk() ||
          !seqDisplayModeOk()) 
      {
         // MSGID=WLI1103 Report version &1 does not exist
         COMPMQ("WLI1103", SYSTP.getLIVR());
         return;
      }
      if (CRCommon.getMode() == cEnumMode.CHANGE) {
         updateSelection = true;
         submitSelection = false;
      }
      // Run
      if (this.XXOPT2.EQ(" 9")) {
         CRCommon.setChangeMode();
         updateSelection = false;
         submitSelection = true;
         picSetMethod('U');
         SQ = 1;
         picPush(SEQ.charAt(SQ - 1), 'I');
         return;
      }
   }
   
   /**
   * B-panel - update.
   */
   public void PBUPD() {
      ensureReadOfTables();
      lastPanel = 'B'; // Set last panel
      DSP.clearOption();
      // Next step
      if (DSP.WBRRNA != 0) {
         picSetMethod('C');
      } else {
         picSetMethod('I');
      }
      // No subfile record
      if (DSP.WBRRNA == 0) {
         return;
      }
      // F12 was pressed - stop processing subfile lines
      if (F12 == DSP.X0FKEY) {
         picSetMethod('D');
         this.XLPIC1 = picGetPrevTopPanel();
         // Check if copy aborted.
         if (this.XLPIC1 == 'C') {
            DSP.XBRRNA = DSP.WBRRNA;
            DSP.updateSFL("BS");
            return;
         }
      }
      // Update just option on subfile line if display mode
      if (this.XXOPT2.EQ(" 5")) {
         DSP.XBRRNA = DSP.WBRRNA;
         DSP.updateSFL("BS");
         return;
      }
      // Move new values to subfile (if not copy)
      if (this.XXOPT2.NE(" 3")) {
         if (SYSTP.CHAIN("00", SYSTP.getKey("00"))) {
            DSP.XBRRNA = DSP.WBRRNA;
         } else {
            IN50 = true;
            IN51 = true;
            DSP.XBRRNA = DSP.XBRRNT;
         }
         PBMSF();
         DSP.updateSFL("BS");
         IN50 = false;
         IN51 = false;
         return;
      }
      // Copy
      DSP.updateSFL("BS");
      // Check if copy was completed
      if (SYSTP.CHAIN("00", SYSTP.getKey("00"))) {
         DSP.XBRRNM++;
         DSP.XBRRNA = DSP.XBRRNM;
         DSP.WBRRNA = DSP.XBRRNM;
         DSP.WSOPT2.clear();
         PBMSF();
         DSP.writeSFL("BS");
         IN95 = true;
      }
   }

   /**
   * B-panel - roll subfile.
   */
   public void PBROL() {
      ensureReadOfTables();
      DSP.XBRRNA = DSP.XBRRNM;
      DSP.restPosSFL('B');
      // Restore last position
      if (DSP.XBRRNM > 0) {
         if (IN61) {
            SYSTP.setLIVR().move(XLLIVR);
            SYSTP.SETGT_SCAN("00", SYSTP.getKey("00"));
         }
         PBROL_SETGT(); // exit point for modification
      }
      if (IN61) {
         found_CSYSTP = SYSTP.READE("00", SYSTP.getKey("00", 4));
      }
      PBROL_READE1(); // exit point for modification
      while (found_CSYSTP && this.XXSPGB > 0) {
         if (SYSTP.isQualifiedForSFL(WSRGTM)) {
            this.XXSPGB--;
            DSP.XBRRNA++;
            DSP.WBRRNA = DSP.XBRRNA;
            DSP.XBRRNM++;
            XLLIVR.move(SYSTP.getLIVR());
            DSP.WSOPT2.clear();
            PBROL_PBMSF(); // exit point for modification
            PBMSF();
            DSP.writeSFL("BS");
         }
         if (IN61) {
            found_CSYSTP = SYSTP.READE("00", SYSTP.getKey("00", 4));
         }
      }
      // End of subfile
      PBROL_READE2(); // exit point for modification
      if (!found_CSYSTP) {
         IN96 = true;
         DSP.writeEofSFL("BS");
      }
      // If records exists then display the subfile
      if (DSP.XBRRNM > 0) {
         IN95 = true;
         DSP.XBRRNA = DSP.XBRRNM - 1;
         DSP.trunkPosSFL('B');
         DSP.XBRRNA++;
      }
   }

   /**
   * B-panel - move information to subfile fields.
   */
   public void PBMSF() {
      DSP.moveDSintoSFLpre("B"); // Do standard sync for SFL buf
      PBMSF_AfterSFLPre();  // exit point for modification
      // Set hidden key fields
      // -
      // Get info from APS455Fnc
      pMaintain = get_pMaintain();
      pMaintain.messages.forgetNotifications();
      pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
      pMaintain.setQuickMode();
      pMaintain.SYSTP = SYSTP;
      pMaintain.passCSYSTP = true;
      passExtensionTable(); // exit point for modification
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      pMaintain.release();
      // Set fields in the list
      // - Report version
      if (pMaintain.LIVR.isAccessDISABLED()) {
         DSP.WSLIVR.clear();
      } else {
         DSP.WSLIVR.move(pMaintain.LIVR.get());
      }
      // - Change ID
      if (pMaintain.CHID.isAccessDISABLED()) {
         DSP.WSCHID.clear();
      } else {
         DSP.WSCHID.move(pMaintain.CHID.get());
      }
      // - Change Date
      if (pMaintain.LMDT.isAccessDISABLED()) {
         DSP.WSLMDT.clear();
      } else {
         DSP.WSLMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.LMDT.get(), LDAZD.DTFM, ' '));
      }
      // - Voucher text
      if (pMaintain.VTXT.isAccessDISABLED()) {
         DSP.WSVTXT.clear();
      } else {
         DSP.WSVTXT.move(pMaintain.VTXT.get());
      }
      PBMSF_BeforeSFLPost(); // exit point for modification
      DSP.moveDSintoSFLpost("B"); // Do standard sync for SFL buf
   }

   /**
   * B-panel - prompt.
   */
   public boolean PBPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PBPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * B-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PBPMT_perform() {
      return false;
   }

   /**
   * Sets focus on a line in the subfile on panel B.
   *
   * @param lineNo
   *    Line number
   */
   public void setFocus_on_subfile_B(int lineNo) { 	
      // Set focus on the line
      DSP.setSubfileFocus("BS", "*ROW", lineNo);
   }

   /**
   * C-panel - initiate.
   */
   public void PCINZ() {
      preparePanelC = true;
      // Call APS455Fnc, copy - step initiate
      // =========================================
      pCopy = get_pCopy();
      pCopy.messages.forgetNotifications();
      pCopy.prepare(cEnumStep.INITIATE);
      // Set key parameters
      if (!passCSYSTP) {
         // Set primary keys
         // - Responsible
         pCopy.RESP.set().clear();
         // - Report version
         pCopy.LIVR.set().moveLeftPad(SYSTP.getLIVR());
      }
      // Pass a reference to the record.
      pCopy.SYSTP = SYSTP;
      pCopy.passCSYSTP = passCSYSTP;
      passCSYSTP = false;
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pCopy);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pCopy.messages, 'C', false);
      // Release resources allocated by the parameter list.
      pCopy.release();
   }

   /**
   * C-panel - display - initiate.
   */
   public void PCDSP_INZ() {
      // Set last panel
      lastPanel = 'C';
      // Check if the display fields should be prepared
      if (preparePanelC) {
         preparePanelC = false;
         PCDSP_INZ_prepare();
      }
   }
   
   /**
   * C-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PCDSP_INZ_prepare() {
      // Report version
      DSP.WCLIVR.moveLeftPad(pCopy.LIVR.get());
      // Copy to report version
      DSP.CCLIVR.moveLeftPad(pCopy.CPLIVR.get());
   }

   /**
   * C-panel - check.
   */
   public void PCCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS455Fnc, delete - step validate
      // =========================================
      pCopy.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PCCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pCopy);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelC = true;
      // =========================================
      // Handle messages
      handleMessages(pCopy.messages, 'C', false);
      if (pCopy.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pCopy.release();
   }
   
   /**
   * C-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PCCHK_prepare() {
      // Copy to report version
      if (pCopy.CPLIVR.isAccessMANDATORYorOPTIONAL()) {
         pCopy.CPLIVR.set().moveLeftPad(DSP.CCLIVR);
      }
      return true;
   }

   /**
   * C-panel - update.
   */
   public void PCUPD() {
      // Declaration
      boolean error = false;
      // Call APS455Fnc, maintain - step update
      // =========================================
      pCopy.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pCopy.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pCopy.messages, transStatus);
      preparePanelC = true;
      // =========================================
      // Handle messages
      error = handleMessages(pCopy.messages, 'C', false);
      // Release resources allocated by the parameter list.
      pCopy.release();
      // Next step
      if (!error) {
         if (this.PXO.charAt(1) == '1') {
            // Update copied data
            this.SEQ.fill('-');
            this.SEQ.moveLeft(DSP.WWPSEQ);
            this.CSSQ.moveLeft(this.SEQ);
            CRCommon.setChangeMode();
            this.SQ = 1;
            picSet(this.SEQ.charAt(this.SQ - 1), 'I');
            updateSelection = true;
            submitSelection = false;
         } else {
            // Return to start panel
            picPop();
         }
         // Set new key values
         SYSTP.setRESP().clear();
         SYSTP.setLIVR().moveLeftPad(pCopy.CPLIVR.get());
      }
   }

   /**
   * C-panel - prompt.
   */
   public boolean PCPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PCPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }
   
   /**
   * C-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PCPMT_perform() {
      // ----------------------------------------------------------------
      // Prompt report version
      if (DSP.hasFocus("CCLIVR")) {
         if (cRefLIVRext.prompt(this, /*maintain*/ false, /*select*/ pCopy.CPLIVR.isAccessMANDATORYorOPTIONAL(), 
             SYSTP, SYSTP.getCONO(), SYSTP.getDIVI(), "APS455", DSP.CCLIVR))
         {
            DSP.CCLIVR.moveLeftPad(this.PXKVA5);
         }
         return true;
      }
      // ----------------------------------------------------------------
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_C(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("LIVR")) {  DSP.setFocus("WCLIVR");
      } else if (FLDI.EQ("CPLIVR")) {  DSP.setFocus("CCLIVR");
      }
   }

   /**
   * D-panel - initiate.
   */
   public void PDINZ() {
      preparePanelD = true;
      // Call APS455Fnc, delete - step initiate
      // =========================================
      pDelete = get_pDelete();
      pDelete.messages.forgetNotifications();
      pDelete.prepare(cEnumStep.INITIATE);
      // Set key parameters
      if (!passCSYSTP) {
         // Set primary keys
         // - Responsible
         pDelete.RESP.set().clear();
         // - Report version
         pDelete.LIVR.set().moveLeftPad(SYSTP.getLIVR());
      }
      // Pass a reference to the record.
      pDelete.SYSTP = SYSTP;
      pDelete.passCSYSTP = passCSYSTP;
      passCSYSTP = false;
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pDelete);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pDelete.messages, 'D', false);
      // Release resources allocated by the parameter list.
      pDelete.release();
   }

   /**
   * D-panel - display - initiate.
   */
   public void PDDSP_INZ() {
      // Set last panel
      lastPanel = 'D';
      // Check if the display fields should be prepared
      if (preparePanelD) {
         preparePanelD = false;
         PDDSP_INZ_prepare();
      }
   }
   
   /**
   * D-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PDDSP_INZ_prepare() {
      // Report version
      DSP.WDLIVR.moveLeftPad(pDelete.LIVR.get());
   }

   /**
   * D-panel - check.
   */
   public void PDCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS455Fnc, delete - step validate
      // =========================================
      pDelete.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PDCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pDelete);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelD = true;
      // =========================================
      // Handle messages
      handleMessages(pDelete.messages, 'D', false);
      if (pDelete.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pDelete.release();
   }
   
   /**
   * D-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PDCHK_prepare() {
      // Nothing to validate
      return true;
   }

   /**
   * D-panel - update.
   */
   public void PDUPD() {
      // Declaration
      boolean error = false;
      // Call APS455Fnc, delete - step update
      // =========================================
      pDelete.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pDelete.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pDelete.messages, transStatus);
      preparePanelD = true;
      // =========================================
      // Handle messages
      error = handleMessages(pDelete.messages, 'D', false);
      // Release resources allocated by the parameter list.
      pDelete.release();
      // Next step
      if (!error) {
         picPop();
      }
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_D(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("LIVR")) {  DSP.setFocus("WDLIVR");
      }
   }

   /**
   * E-panel - initiate.
   */
   public void PEINZ() {
      preparePanelE = true;
      // Call APS455Fnc, maintain - step initiate
      // =========================================
      pMaintain = get_pMaintain();
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.INITIATE);
      if (!updateSelection) {
         pMaintain.noUpdate.set(true);
      }
      if (!submitSelection || !isLastPanel()) {
         pMaintain.noSubmit.set(true);
      }
      pMaintain.manualCall.set(true); // Called manually from APS455 (as opposed to automatically from a batch job, e.g. PPCRTSBT).
      // Set key parameters
      if (!passCSYSTP) {
         // Set primary keys
         // - Responsible
         pMaintain.RESP.set().moveLeftPad(SYSTP.getRESP());
         // - Report Version
         pMaintain.LIVR.set().moveLeftPad(SYSTP.getLIVR());
      }
      // Set Other parameters
      // - Division
      pMaintain.selectDIVI.set().moveLeftPad(presetDIVI);
      if (presetINBN == 0L) {
         // Only preset first time
         presetDIVI.clear();
      }
      // - Invoice batch number
      pMaintain.fromINBN.set(presetINBN);
      // - Invoice batch operation
      pMaintain.IBOP.set(presetIBOP);
      // - Accounting date
      if (submitSelection && setACDTtoCurrentDate) {
         pMaintain.ACDT.set(movexDate());
      }
      setACDTtoCurrentDate = false; // Only set first time, not when returning to the panel
      // Pass a reference to the record.
      pMaintain.SYSTP = SYSTP;
      pMaintain.passCSYSTP = passCSYSTP;
      if (updateSelection) {
         // Don't change passCSYSTP flag to false if updateSelection is false.
         // - Having updateSelection = false and keeping passCSYSTP = true ensures that 
         // successive panels work with the fields in CSYSTP like a working copy 
         // without updating or reading the DB.
         passCSYSTP = false;
      }
      passExtensionTable(); // exit point for modification
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pMaintain.messages, 'E', false);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Check whether to abort bookmark
      if (errorOnInitiate && CRCommon.abortBookmark()) {
         SETLR();
      }
   }
   
   /**
   * E-panel - display - initiate.
   */
   public void PEDSP_INZ() {
      // Set last panel
      lastPanel = 'E';
      // Check if the display fields should be prepared
      if (preparePanelE) {
         preparePanelE = false;
         PEDSP_INZ_prepare();
      }
   }
   
   /**
   * E-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PEDSP_INZ_prepare() {
      // Report version
      DSP.WELIVR.moveLeftPad(pMaintain.LIVR.get());
      IN01 = pMaintain.LIVR.isAccessOUT();
      IN21 = pMaintain.LIVR.isAccessDISABLED();
      // Division
      DSP.WEDIVI.moveLeftPad(pMaintain.selectDIVI.get());
      IN02 = pMaintain.selectDIVI.isAccessOUT();
      IN22 = pMaintain.selectDIVI.isAccessDISABLED();
      // Supplier invoice no - from
      DSP.WFSINO.moveLeftPad(pMaintain.fromSINO.get());
      IN03 = pMaintain.fromSINO.isAccessOUT();
      IN23 = pMaintain.fromSINO.isAccessDISABLED();
      // Supplier invoice no - to
      DSP.WTSINO.moveLeftPad(pMaintain.toSINO.get());
      IN04 = pMaintain.toSINO.isAccessOUT();
      IN24 = pMaintain.toSINO.isAccessDISABLED();
      // Invoice batch number - from
      DSP.WFINBN = pMaintain.fromINBN.get();
      IN05 = pMaintain.fromINBN.isAccessOUT();
      IN25 = pMaintain.fromINBN.isAccessDISABLED();
      // Invoice batch number - to
      DSP.WTINBN = pMaintain.toINBN.get();
      IN06 = pMaintain.toINBN.isAccessOUT();
      IN26 = pMaintain.toINBN.isAccessDISABLED();
      // Invoice batch type - from
      DSP.WFIBTP.moveLeftPad(pMaintain.fromIBTP.get());
      IN07 = pMaintain.fromIBTP.isAccessOUT();
      IN27 = pMaintain.fromIBTP.isAccessDISABLED();
      // Invoice batch type - to
      DSP.WTIBTP.moveLeftPad(pMaintain.toIBTP.get());
      // Invoice status - from
      DSP.WFSUPA = pMaintain.fromSUPA.get();
      IN08 = pMaintain.fromSUPA.isAccessOUT();
      IN28 = pMaintain.fromSUPA.isAccessDISABLED();
      // Invoice status - to
      DSP.WTSUPA = pMaintain.toSUPA.get();
      // Supplier - from
      DSP.WFSUNO.moveLeftPad(pMaintain.fromSUNO.get());
      IN09 = pMaintain.fromSUNO.isAccessOUT();
      IN29 = pMaintain.fromSUNO.isAccessDISABLED();
      // Supplier - to
      DSP.WTSUNO.moveLeftPad(pMaintain.toSUNO.get());
      // Invoice date - from
      DSP.WFIVDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.fromIVDT.get(), LDAZD.DTFM, ' '));
      IN10 = pMaintain.fromIVDT.isAccessOUT();
      IN30 = pMaintain.fromIVDT.isAccessDISABLED();
      // Invoice date - to
      DSP.WTIVDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.toIVDT.get(), LDAZD.DTFM, ' '));
      // Authorized user - from
      DSP.WFAPCD.moveLeftPad(pMaintain.fromAPCD.get());
      IN11 = pMaintain.fromAPCD.isAccessOUT();
      IN31 = pMaintain.fromAPCD.isAccessDISABLED();
      // Authorized user - to
      DSP.WTAPCD.moveLeftPad(pMaintain.toAPCD.get());
      // Invoice batch head errors - from
      DSP.WFIBHE = pMaintain.fromIBHE.get();
      IN12 = pMaintain.fromIBHE.isAccessOUT();
      IN32 = pMaintain.fromIBHE.isAccessDISABLED();
      // Invoice batch head errors - to
      DSP.WTIBHE = pMaintain.toIBHE.get();
      // Invoice batch line errors - from
      DSP.WFIBLE = pMaintain.fromIBLE.get();
      IN13 = pMaintain.fromIBLE.isAccessOUT();
      IN33 = pMaintain.fromIBLE.isAccessDISABLED();
      // Invoice batch line errors - to
      DSP.WTIBLE = pMaintain.toIBLE.get();
      // Invoice batch operation
      DSP.WEIBOP = pMaintain.IBOP.get();
      IN17 = pMaintain.IBOP.isAccessOUT();
      IN37 = pMaintain.IBOP.isAccessDISABLED();
      // Report layout
      DSP.WELITP = pMaintain.LITP.get();
      IN18 = pMaintain.LITP.isAccessOUT();
      IN38 = pMaintain.LITP.isAccessDISABLED();
      // Accounting date
      DSP.WEACDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.ACDT.get(), LDAZD.DTFM, ' '));
      IN19 = pMaintain.ACDT.isAccessOUT();
      IN39 = pMaintain.ACDT.isAccessDISABLED();
      // Voucher text
      DSP.WEVTXT.moveLeftPad(pMaintain.VTXT.get());
      IN20 = pMaintain.VTXT.isAccessOUT();
      IN40 = pMaintain.VTXT.isAccessDISABLED();
      // Entry date
      DSP.WERGDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.RGDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change date
      DSP.WELMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.LMDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change ID
      DSP.WECHID.moveLeftPad(pMaintain.CHID.get());
   }

   /**
   * E-panel - display - roll.
   */
   public void PEDSP_X0E78R() {
      roll(DSP.WELIVR, "WELIVR");
   }

   /**
   * E-panel - check.
   */
   public void PECHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS455Fnc, maintain - step validate
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PECHK_prepare()) {
         return;
      }
      // Pass a reference to the record.
      pMaintain.SYSTP = SYSTP;
      passExtensionTable(); // exit point for modification
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelE = true;
      // =========================================
      // Handle messages
      handleMessages(pMaintain.messages, 'E', false);
      if (pMaintain.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pMaintain.release();
   }

   /**
   * E-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PECHK_prepare() {
      // Report version
      if (pMaintain.LIVR.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.LIVR.set().moveLeftPad(DSP.WELIVR);
      }
      // Division
      if (pMaintain.selectDIVI.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.selectDIVI.set().moveLeftPad(DSP.WEDIVI);
      }
      // Supplier invoice no - from
      if (pMaintain.fromSINO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.fromSINO.set().moveLeftPad(DSP.WFSINO);
      }
      // Supplier invoice no - to
      if (pMaintain.toSINO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.toSINO.set().moveLeftPad(DSP.WTSINO);
      }
      // Invoice batch number - from
      if (pMaintain.fromINBN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.fromINBN.set(DSP.WFINBN);
      }
      // Invoice batch number - to
      if (pMaintain.toINBN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.toINBN.set(DSP.WTINBN);
      }
      // Invoice batch type - from
      if (pMaintain.fromIBTP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.fromIBTP.set().moveLeftPad(DSP.WFIBTP);
      }
      // Invoice batch type - to
      if (pMaintain.toIBTP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.toIBTP.set().moveLeftPad(DSP.WTIBTP);
      }
      // Invoice status - from
      if (pMaintain.fromSUPA.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.fromSUPA.set(DSP.WFSUPA);
      }
      // Invoice status - to
      if (pMaintain.toSUPA.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.toSUPA.set(DSP.WTSUPA);
      }
      // Supplier - from
      if (pMaintain.fromSUNO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.fromSUNO.set().moveLeftPad(DSP.WFSUNO);
      }
      // Supplier - to
      if (pMaintain.toSUNO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.toSUNO.set().moveLeftPad(DSP.WTSUNO);
      }
      // Invoice date - from
      if (pMaintain.fromIVDT.isAccessMANDATORYorOPTIONAL()) {
         if (DSP.WFIVDT.isBlank()) {
            pMaintain.fromIVDT.set(0);
         } else {
            if (!CRCalendar.convertDate_blankDIVI(DSP.WFIVDT, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WFIVDT");
               // MSGID=WIVD101 Invoice date &1 is invalid
               COMPMQ("WIVD101", DSP.WFIVDT);
               return false;
            }
            pMaintain.fromIVDT.set(CRCalendar.getDate());
         }
      }
      // Invoice date - to
      if (pMaintain.toIVDT.isAccessMANDATORYorOPTIONAL()) {
         if (DSP.WTIVDT.isBlank()) {
            pMaintain.toIVDT.set(0);
         } else {
            if (!CRCalendar.convertDate_blankDIVI(DSP.WTIVDT, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WTIVDT");
               // MSGID=WIVD101 Invoice date &1 is invalid
               COMPMQ("WIVD101", DSP.WTIVDT);
               return false;
            }
            pMaintain.toIVDT.set(CRCalendar.getDate());
         }
      }
      // Authorized user - from
      if (pMaintain.fromAPCD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.fromAPCD.set().moveLeftPad(DSP.WFAPCD);
      }
      // Authorized user - to
      if (pMaintain.toAPCD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.toAPCD.set().moveLeftPad(DSP.WTAPCD);
      }
      // Invoice batch head errors - from
      if (pMaintain.fromIBHE.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.fromIBHE.set(DSP.WFIBHE);
      }
      // Invoice batch head errors - to
      if (pMaintain.toIBHE.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.toIBHE.set(DSP.WTIBHE);
      }
      // Invoice batch line errors - from
      if (pMaintain.fromIBLE.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.fromIBLE.set(DSP.WFIBLE);
      }
      // Invoice batch line errors - to
      if (pMaintain.toIBLE.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.toIBLE.set(DSP.WTIBLE);
      }
      // Invoice batch operation
      if (pMaintain.IBOP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.IBOP.set(DSP.WEIBOP);
      }
      // Report layout
      if (pMaintain.LITP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.LITP.set(DSP.WELITP);
      }
      // Accounting date
      if (pMaintain.ACDT.isAccessMANDATORYorOPTIONAL()) {
         if (!CRCalendar.convertDate_blankDIVI(DSP.WEACDT, LDAZD.DTFM)) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEACDT");
            // MSGID=WACD101 Accounting date &1 is invalid
            COMPMQ("WACD101", DSP.WEACDT);
            return false;
         }
         pMaintain.ACDT.set(CRCalendar.getDate());
      }
      // Voucher text
      if (pMaintain.VTXT.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.VTXT.set().moveLeftPad(DSP.WEVTXT);
      }
      return true;
   }
   
   /**
   * E-panel - update.
   */
   public void PEUPD() {
      // Declaration
      boolean error = false;
      // Call APS455Fnc, maintain - step update
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.UPDATE);
      if (!selectSubmitParameters()) {
         CROutput.clearOutputDefinitions(pMaintain.BJNO.get(), 
            MNS213DS, rAPIDS, APIDS, PXOPC, pMNS213);
      }
      if (pMaintain.noSubmit.get() || F03 != LDAZZ.FKEY && F12 != LDAZZ.FKEY) {
         // Pass a reference to the record.
         pMaintain.SYSTP = SYSTP;
         passExtensionTable(); // exit point for modification
         // =========================================
         int transStatus = executeTransaction(pMaintain.getTransactionName(), /*returnOnFailure*/ true);
         CRCommon.setDBTransactionErrorMessage(pMaintain.messages, transStatus);
         preparePanelE = true;
         // =========================================
         // Handle messages
         error = handleMessages(pMaintain.messages, 'E', false);
         // Release resources allocated by the parameter list.
         pMaintain.release();
      }
      // Next step
      if (!error) {
         if (presetIBOP != 0 && isLastPanel()) {
            // Leave program if started via option of function key in APS450
            picFinish();
            return;
         }
         nextStep(/*lastPanelInAddMode*/ true);
      }
   }

   /**
   * E-panel - prompt.
   */
   public boolean PEPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PEPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }
   
   /**
   * E-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PEPMT_perform() {
      // Check selected division
      if (CRCommon.isCentralUser()) {
         selectedDIVI.moveLeftPad(DSP.WEDIVI);
      } else {
         selectedDIVI.moveLeftPad(LDAZD.DIVI);
      }
      // ----------------------------------------------------------------
      // Prompt Division
      if (DSP.hasFocus("WEDIVI")) {
         if (cRefDIVIext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.selectDIVI.isAccessMANDATORYorOPTIONAL(), 
             MNDIV, SYSTP.getCONO(), DSP.WEDIVI)) 
         {
            DSP.WEDIVI.moveLeftPad(this.PXKVA2);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Invoice batch number
      if (DSP.hasFocus("WFINBN")) {
         if (cRefINBNext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.fromINBN.isAccessMANDATORYorOPTIONAL(), 
             APIBH, SYSTP.getCONO(), selectedDIVI, DSP.WFINBN)) 
         {
            DSP.WFINBN = this.PXKVA3.getLong(cRefINBN.length(), 0);
         }
         return true;
      }
      if (DSP.hasFocus("WTINBN")) {
         if (cRefINBNext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.toINBN.isAccessMANDATORYorOPTIONAL(), 
             APIBH, SYSTP.getCONO(), selectedDIVI, DSP.WTINBN)) 
         {
            DSP.WTINBN = this.PXKVA3.getLong(cRefINBN.length(), 0);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Supplier
      if (DSP.hasFocus("WFSUNO")) {
         if (cRefSUNOext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.fromSUNO.isAccessMANDATORYorOPTIONAL(), 
             IDMAS, SYSTP.getCONO(), DSP.WFSUNO)) 
         {
            DSP.WFSUNO.moveLeftPad(this.PXKVA2);
         }
         return true;
      }
      if (DSP.hasFocus("WTSUNO")) {
         if (cRefSUNOext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.toSUNO.isAccessMANDATORYorOPTIONAL(), 
             IDMAS, SYSTP.getCONO(), DSP.WTSUNO)) 
         {
            DSP.WTSUNO.moveLeftPad(this.PXKVA2);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Authorized user
      if (DSP.hasFocus("WFAPCD")) {
         if (cRefAPCDext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.fromAPCD.isAccessMANDATORYorOPTIONAL(), 
             APRCD, SYSTP.getCONO(), selectedDIVI, DSP.WFAPCD)) 
         {
            DSP.WFAPCD.moveLeftPad(this.PXKVA3);
         }
         return true;
      }
      if (DSP.hasFocus("WTAPCD")) {
         if (cRefAPCDext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.toAPCD.isAccessMANDATORYorOPTIONAL(), 
             APRCD, SYSTP.getCONO(), selectedDIVI, DSP.WTAPCD)) 
         {
            DSP.WTAPCD.moveLeftPad(this.PXKVA3);
         }
         return true;
      }
      // ----------------------------------------------------------------
      return false;
   }
   
   /**
   * Checks if the field ID is blank or if it is located on the panel.
   *
   * @param FLDI
   *    Field ID
   * @return
   *    Returns true if the field is blank or is on the panel.
   */
   public boolean isFieldOnPanel_E(MvxString FLDI) {
      if (FLDI.isBlank()
          || FLDI.EQ("LIVR")
          || FLDI.EQ("selectDIVI")
          || FLDI.EQ("fromSINO")
          || FLDI.EQ("toSINO")
          || FLDI.EQ("fromINBN")
          || FLDI.EQ("toINBN")
          || FLDI.EQ("fromIBTP")
          || FLDI.EQ("toIBTP")
          || FLDI.EQ("fromSUPA")
          || FLDI.EQ("toSUPA")
          || FLDI.EQ("fromSUNO")
          || FLDI.EQ("toSUNO")
          || FLDI.EQ("fromIVDT")
          || FLDI.EQ("toIVDT")
          || FLDI.EQ("fromAPCD")
          || FLDI.EQ("toAPCD")
          || FLDI.EQ("fromIBHE")
          || FLDI.EQ("toIBHE")
          || FLDI.EQ("fromIBLE")
          || FLDI.EQ("toIBLE")
          || FLDI.EQ("IBOP")
          || FLDI.EQ("LITP")
          || FLDI.EQ("ACDT")
          || FLDI.EQ("VTXT")
         )
      {
         return true;
      }
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_E(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("LIVR")) {  DSP.setFocus("WELIVR");
      } else if (FLDI.EQ("selectDIVI")) {  DSP.setFocus("WEDIVI");
      } else if (FLDI.EQ("fromSINO")) {  DSP.setFocus("WFSINO");
      } else if (FLDI.EQ("toSINO")) {  DSP.setFocus("WTSINO");
      } else if (FLDI.EQ("fromINBN")) {  DSP.setFocus("WFINBN");
      } else if (FLDI.EQ("toINBN")) {  DSP.setFocus("WTINBN");
      } else if (FLDI.EQ("fromIBTP")) {  DSP.setFocus("WFIBTP");
      } else if (FLDI.EQ("toIBTP")) {  DSP.setFocus("WTIBTP");
      } else if (FLDI.EQ("fromSUPA")) {  DSP.setFocus("WFSUPA");
      } else if (FLDI.EQ("toSUPA")) {  DSP.setFocus("WTSUPA");
      } else if (FLDI.EQ("fromSUNO")) {  DSP.setFocus("WFSUNO");
      } else if (FLDI.EQ("toSUNO")) {  DSP.setFocus("WTSUNO");
      } else if (FLDI.EQ("fromIVDT")) {  DSP.setFocus("WFIVDT");
      } else if (FLDI.EQ("toIVDT")) {  DSP.setFocus("WTIVDT");
      } else if (FLDI.EQ("fromAPCD")) {  DSP.setFocus("WFAPCD");
      } else if (FLDI.EQ("toAPCD")) {  DSP.setFocus("WTAPCD");
      } else if (FLDI.EQ("fromIBHE")) {  DSP.setFocus("WFIBHE");
      } else if (FLDI.EQ("toIBHE")) {  DSP.setFocus("WTIBHE");
      } else if (FLDI.EQ("fromIBLE")) {  DSP.setFocus("WFIBLE");
      } else if (FLDI.EQ("toIBLE")) {  DSP.setFocus("WTIBLE");
      } else if (FLDI.EQ("IBOP")) {  DSP.setFocus("WEIBOP");
      } else if (FLDI.EQ("LITP")) {  DSP.setFocus("WELITP");
      } else if (FLDI.EQ("ACDT")) {  DSP.setFocus("WEACDT");
      } else if (FLDI.EQ("VTXT")) {  DSP.setFocus("WEVTXT");
      }
   }

   /**
   * P-panel - initiate.
   */
   public void PPINZ() {
      preparePanelP = true;
      // Call APS455Fnc, settings - step initiate
      // =========================================
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      // Set key parameters
      // - Responsible
      pSettings.RESP.set().move(LDAZD.RESP);
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pSettings.messages, 'P', false);
      // Release resources allocated by the parameter list.
      pSettings.release();
   }
   
   /**
   * P-panel - display - initiate.
   */
   public void PPDSP_INZ() {
      // Set last panel
      lastPanel = 'P';
      // Check if the display fields should be prepared
      if (preparePanelP) {
         preparePanelP = false;
         PPDSP_INZ_prepare();
      }
   }
   
   /**
   * P-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PPDSP_INZ_prepare() {
      // Opening panel
      DSP.WWSPIC = pSettings.SPIC.get().getChar();
      // Default panel sequence
      DSP.WWDSEQ.move(pSettings.DSEQ.get());
   }

   /**
   * P-panel - check.
   */
   public void PPCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS455Fnc, settings - step validate
      // =========================================
      pSettings.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PPCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelP = true;
      // =========================================
      // Handle messages
      handleMessages(pSettings.messages, 'P', false);
      if (pSettings.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pSettings.release();
   }

   /**
   * P-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PPCHK_prepare() {
      // Opening panel
      if (pSettings.SPIC.isAccessMANDATORYorOPTIONAL()) {
         pSettings.SPIC.set().move(DSP.WWSPIC);
      }
      // Default panel sequence
      if (pSettings.DSEQ.isAccessMANDATORYorOPTIONAL()) {
         pSettings.DSEQ.set().move(DSP.WWDSEQ);
         if (!seqCheckSeq(getAllowedPanels(), DSP.WWDSEQ)) {
            return false;
         }
      }
      return true;
   }
   
   /**
   * P-panel - update.
   */
   public void PPUPD() {
      // Declaration
      boolean error = false;
      // Call APS455Fnc, settings - step update
      // =========================================
      pSettings.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
      preparePanelP = true;
      // =========================================
      // Handle messages
      error = handleMessages(pSettings.messages, 'P', false);
      // Release resources allocated by the parameter list.
      pSettings.release();
      // Next step
      if (!error) {
         DSP.WWPSEQ.move(pSettings.DSEQ.get());
         this.CSSQ.moveLeft(this.SEQ);
         initStartPicture();
      }
   }
   
   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_P(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("SPIC")) {  DSP.setFocus("WWSPIC");
      } else if (FLDI.EQ("DSEQ")) {  DSP.setFocus("WWDSEQ");
      }
   }
   
   /**
   * Roll detail panel.
   * Switches to new record on the detail panels.
   * @param LIVR
   *    The current value.
   * @param fieldName
   *    The name of the display field to set focus on in error message.
   */
   public void roll(MvxString LIVR, String fieldName) {
      // Roll forward
      if (FROLLU == DSP.X0FKEY || F08 == DSP.X0FKEY) {
         SYSTP.SETGT("00", SYSTP.getKey("00"));
         if (SYSTP.READE("00", SYSTP.getKey("00", 4))) {
            picSetMethod('I');
            return;
         } else {
            SYSTP.SETLL("00", SYSTP.getKey("00"));
            if (SYSTP.READE("00", SYSTP.getKey("00", 4))) {
               picSetMethod('I');
               // MSGID=XRO0001 You have scrolled past first or last record in file
               COMPMQ("XRO0001");
               return;
            }
         }
      }
      // Roll backward
      if (FROLLD == DSP.X0FKEY || F07 == DSP.X0FKEY) {
         SYSTP.SETLL("00", SYSTP.getKey("00"));
         if (SYSTP.REDPE("00", SYSTP.getKey("00", 4))) {
            picSetMethod('I');
            return;
         } else {
            SYSTP.SETLL("00", SYSTP.getKey("00"));
            if (SYSTP.READE("00", SYSTP.getKey("00", 4))) {
               picSetMethod('I');
               // MSGID=XRO0001 You have scrolled past first or last record in file
               COMPMQ("XRO0001");
               return;
            }
         }
      }
      // Check if keyfield is changed
      if (SYSTP.getLIVR().NE(LIVR)) {
         // Save key values
         SSLIVR.move(SYSTP.getLIVR());
         SYSTP.setLIVR().move(LIVR);
         if (SYSTP.CHAIN("00", SYSTP.getKey("00"))) {
            picSetMethod('I');
            return;
         }
         // Restore key values
         SYSTP.setLIVR().move(SSLIVR);
         picSetMethod('D');
         IN60 = true;
         DSP.setFocus(fieldName);
         // MSGID=WLI1103 Report version &1 does not exist
         COMPMQ("WLI1103", LIVR);
         return;
      } else {
         picSetMethod('I');
         picPush(seqSwitchToNextPanel(), 'I');
         return;
      }
   }

   /**
   * Lets the user configure submit parameters by calling MNS212 and MNS230.
   * @return
   *    False if selection of submit parameters was aborted with F03 or F12.
   *    True otherwise.
   */
   public boolean selectSubmitParameters() {
      if (pMaintain.noSubmit.get()) {
         // The selection is not to be submitted.
         return true;
      }
      // Get new batch jobnumber
      pMaintain.BJNO.set().moveLeftPad(this.getBJNO());
      // Select printer data
      if (pMaintain.IBOP.get() == cRefIBOPext.PRINT()) {
         // Invoice
         CROutput.selectOutputDefs(LDAZD.CONO, LDAZD.DIVI, pMaintain.BJNO.get(),
            "APS456PF", PXMNS210);
         // Abort if F03 or F12 pressed
         if (F03 == LDAZZ.FKEY || F12 == LDAZZ.FKEY) {
            return false;
         }
         // Detailed printout
         if (pMaintain.LITP.get() == 2) {
            CROutput.selectOutputDefs(LDAZD.CONO, LDAZD.DIVI, pMaintain.BJNO.get(),
               "APS457PF", PXMNS210);
            // Abort if F03 or F12 pressed
            if (F03 == LDAZZ.FKEY || F12 == LDAZZ.FKEY) {
               return false;
            }
         }
      }
      if (pMaintain.IBOP.get() == cRefIBOPext.UPDATE_TO_APL()) {
         CROutput.selectOutputDefs(LDAZD.CONO, LDAZD.DIVI, pMaintain.BJNO.get(),
            "GLS041PF", PXMNS210);
         // Abort if F03 or F12 pressed
         if (F03 == LDAZZ.FKEY || F12 == LDAZZ.FKEY) {
            return false;
         }
      }
      // Select job data
      CROutput.selectJobAttributes(LDAZD.CONO, LDAZD.DIVI, pMaintain.BJNO.get(),
         /*JOB*/ "APS455", /*PGNM*/ "APS455Sbm", PXMNS230);
      // Abort if F03 or F12 pressed
      if (F03 == LDAZZ.FKEY || F12 == LDAZZ.FKEY) {
         return false;
      }
      pMaintain.QCMD.set().moveLeftPad(PXMNS230.DSQCMD);
      return true;
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   * @param panel
   *    The current panel.
   * @param suppressNotification
   *    Indicates if notification messages should be suppressed.
   * @return
   *    Returns true if an error occured. If errors are received during step
   * initiate, then it is not allowed to proceed with steps validate and update.
   */
   public boolean handleMessages(cCRMessageList messages, char panel, boolean suppressNotification) {
      // Declaration
      boolean error = false;
      boolean notification = false;
      boolean clearOption = false;
      // handle messages
      if (messages.exist()) {
         // Check for errors relating to the given panel.
         messages.prepareGetNext();
         while (messages.getNextError(CRMessageDS)) {
            if (panel != 'E'
                || panel == 'E' && isFieldOnPanel_E(CRMessageDS.getPXFLDI())
               )
            {
               error = true;
               break;
            }
         }
         // Check for notifications relating to the given panel.
         if (!error) {
            messages.prepareGetNext();
            while (messages.getNextNotification(CRMessageDS)) {
               if (panel != 'E'
                   || panel == 'E' && isFieldOnPanel_E(CRMessageDS.getPXFLDI())
                  )
               {
                  messages.rememberNotification(CRMessageDS.getPXMSID().toStringRTrim());
                  notification = true;
                  break;
               }
            }
         }
         // Issue message
         if (error || notification) {
            if (error && lastPanel =='B') {
               // Display message in B-panel
               switch (panel) {
                  case 'B':
                     clearOption = true;
                     break;
                  case 'C':
                     clearOption = true;
                     PCDSP_F12();
                     break;
                  case 'D':
                     clearOption = true;
                     PDDSP_F12();
                     break;
                  case 'E':
                     clearOption = true;
                     PEDSP_F12();
                     break;
                  case 'P':
                     PPDSP_F12();
                     break;
               }
            } else {
               // Display message in detail panel
               switch (panel) {
                  case 'C':
                     setFocusOnPanel_C(CRMessageDS.getPXFLDI());
                     break;
                  case 'D':
                     setFocusOnPanel_D(CRMessageDS.getPXFLDI());
                     break;
                  case 'E':
                     setFocusOnPanel_E(CRMessageDS.getPXFLDI());
                     break;
                  case 'P':
                     setFocusOnPanel_P(CRMessageDS.getPXFLDI());
                     break;
               }
            }
            if (error || notification && !suppressNotification) {
               CRCommon.COMPMQ(CRMessageDS);
            }
         } else if (messages.overflow()){
            // Didn't find any message relating to the given panel due to message overflow
            error = true;
            // MSGID=X_00035 Couldn't validate the panel due to many messages on other panels
            COMPMQ("X_00035");
         }
         if (error || notification && !suppressNotification) {
            if (clearOption && DSP.WBRRNA != 0) {
               DSP.clearOption();
               DSP.XBRRNA = DSP.WBRRNA;
               DSP.updateSFL("BS");
               setFocus_on_subfile_B(DSP.WBRRNA);
            }
            picSetMethod('D');
            IN60 = true;
         }
      }
      return error;
   }
   
   /**
   * @return
   *    True if the current panel is the last panel in the sequence.
   */
   public boolean isLastPanel() {
      char nextPanel = SEQ.charAt(SQ);
      return nextPanel == '-' || nextPanel == ' ';
   }

   /**
   * Initializes the start picture.
   * Initializes picture stack, start picture.
   * CHAINS report version - CSYSTP - if started from detail panels.
   */
   public void initStartPicture() {
      found_CSYSTP = false;
      passCSYSTP = false;
      updateSelection = true; // default behavior is to update selection
      submitSelection = true; // default behavior is to submit selection
      if (pSettings.SPIC.get().getChar() == 'B') {
         // select shared report versions
         SYSTP.setRESP().clear(); 
      } else {
         // select personal report version
         SYSTP.setRESP().moveLeftPad(this.DSUSS); 
         SYSTP.setLIVR().moveLeftPad(this.DSUSS);
         // Check record
         found_CSYSTP = SYSTP.CHAIN("00", SYSTP.getKey("00"));
         passCSYSTP = found_CSYSTP;
         if (found_CSYSTP) {
            this.SEQ.fill('-');
            this.SEQ.moveLeft(DSP.WWPSEQ);
            this.XXOPT2.move(" 2");
            moveToIN(41, "01000");
         } else {
            this.SEQ.fill('-');
            this.SEQ.moveLeft(getDefaultPanels());
            moveToIN(41, "10000");
            this.XXOPT2.move(" 1");
         }
         this.CSSQ.moveLeft(this.SEQ);
         this.SQ = 1;
      }
      // Set picture stack
      picSetStack(1, pSettings.SPIC.get().getChar(), 'I');
   }
   
   /**
   * Steps to the next panel.
   * @param lastPanelInAddMode
   *    Indicates if this is the last required panel in add mode.
   */
   public void nextStep(boolean lastPanelInAddMode) {
      if (pMaintain.noSubmit.get() || 
          pSettings.SPIC.get().getChar() == 'B' && F03 != LDAZZ.FKEY && F12 != LDAZZ.FKEY) {
         // switch to next panel
         // (If the batch job is to be submitted then noSubmit becomes false 
         //  for the last panel in the sequence).
         picSetMethod('D');
         if (CRCommon.getMode() == cEnumMode.ADD && lastPanelInAddMode) {
            // End of ADD mode
            picSetMethod('I');
            CRCommon.setChangeMode();
         }
         picPush(seqSwitchToNextPanel(), 'I');
      } else if (F03 == LDAZZ.FKEY) {
         // F3 pressed in MNS212 or MNS230
         picFinish();
      } else if (F12 == LDAZZ.FKEY) {
         // F12 pressed in MNS212 or MNS230
         // Display panel again
      } else {
         // Forget notifications
         if (pMaintain != null) {
            pMaintain.messages.forgetNotifications();
         }
         // Return to start panel
         initStartPicture();
      }
   }

   /** 
   * Ensures that tables that are read via cRef-get methods are actually read in the next method call.
   * This is ensured by setting the found-flags to false.
   */
   public void ensureReadOfTables() {
      // No tables read with cRef-get methods.
   }
   
   /**
   * Processes the bookmark.
   * Prepares the program to process the bookmark with the option given in the bookmark.
   *
   * @return
   *    false if the program shall terminate after processing the bookmark.
   */
   public boolean processBookmark() { 	
      // Get bookmark 	
      Bookmark bookmark = getBookmark(); 	
      // Process bookmark 	
      if (bookmark.getTableName().EQ("CSYSTP")) { 	
         // Validate key values in the bookmark 	
         if(!bookmark.isValidIntField("CONO") || 	
            !bookmark.isValidStringField("DIVI") ||
            !bookmark.isValidStringField("PGNM") ||
            !bookmark.isValidStringField("RESP") ||
            !bookmark.isValidStringField("LIVR"))
         { 	
            // MSGID=X_00025 Invalid bookmark 	
            bookmark.setError("X_00025", ""); 	
            return false; 	
         } 	
         // Unpack key values from the bookmark 	
         bookmark_CONO = bookmark.getIntField("CONO"); 	
         bookmark_DIVI.moveLeftPad(bookmark.getStringField("DIVI")); 	
         bookmark_PGNM.moveLeftPad(bookmark.getStringField("PGNM")); 	
         bookmark_RESP.moveLeftPad(bookmark.getStringField("RESP")); 	
         bookmark_LIVR.moveLeftPad(bookmark.getStringField("LIVR")); 	
         // Check context of the bookmark 	
         if (bookmark_CONO != LDAZD.CONO || bookmark_DIVI.NE(LDAZD.DIVI)) { 	
            //   MSGID=X_00027 You must log on to company &1 and division &2 before using the bookmark 	
            bookmark.setError("X_00027", formatToString(bookmark_CONO, 3) + bookmark_DIVI.toString()); 	
            return false; 	
         } 	
         if (bookmark_PGNM.isBlank()) {
            bookmark_PGNM.moveLeftPad(this.DSPGM);
         }
         if (bookmark_PGNM.NE(this.DSPGM)) {
            // MSGID=X_00025 Invalid bookmark 	
            bookmark.setError("X_00025", ""); 	
            return false; 	
         }
         // Check if trying to open another users personal report settings
         if (!bookmark_RESP.isBlank() && bookmark_RESP.NE(this.DSUSS)) {
            // MSGID=WRE0101 Responsible &1 is invalid
            bookmark.setError("WRE0101", bookmark_RESP.toString());
            return false; 	
         }
         // Retrieve record 	
         SYSTP.setCONO(bookmark_CONO); 	
         SYSTP.setDIVI().move(bookmark_DIVI);
         SYSTP.setPGNM().move(bookmark_PGNM);
         SYSTP.setRESP().move(bookmark_RESP);
         SYSTP.setLIVR().move(bookmark_LIVR);
         if (!SYSTP.CHAIN("00", SYSTP.getKey("00"))) { 	
            SYSTP.clearNOKEY("00"); 	
         }
         // Receive bookmark parameters
         if (bookmark.parametersExist()) {                                                             
            // - Preset selected division (only for personal report version).
            if (!bookmark_RESP.isBlank()) {
               if (bookmark.isValidStringParameter("presetDIVI")) {
                  presetDIVI.moveLeftPad(bookmark.getStringParameter("presetDIVI"));
               }
            }
            // - Preset invoice INBN (only for personal report version).
            if (!bookmark_RESP.isBlank()) {
               if (bookmark.isValidLongParameter("presetINBN")) {
                  presetINBN = bookmark.getLongParameter("presetINBN");
               }
            }
            // - Preset operation IBOP (only for personal report version).
            if (!bookmark_RESP.isBlank()) {
               if (bookmark.isValidIntParameter("presetIBOP")) {
                  presetIBOP = bookmark.getIntParameter("presetIBOP");
               }
            }
         }
         // Handle personal report version
         if (!bookmark_RESP.isBlank()) {
            // Note: personal report versions don't consider all bookmark attributes
            pSettings.SPIC.set().moveLeft(DSP.WWPSEQ); // Set to first panel in sequence
            initStartPicture();
            return true;
         }
         // Handle common report versions:
         // Determine inquiry settings 	
         startPanel = 'B'; // Always use B
         startPanelSequence.move(bookmark.getPanelSequence(DSP.WWPSEQ)); 	
         startSortingOrder = bookmark.getInquiryType(DSP.WBQTTP);
         pSettings.SPIC.set().moveLeft(startPanel);
         // Check option '9 - Run'
         if (bookmark.getOption().EQ(" 9")) {
            // processOptionInBookmark cannot handle opt 9
            bookmark.setOption(" 2"); 
            option9FromBookmark = true;
         }
         // Process the bookmark
         if (startPanel == 'B' && startSortingOrder == 1) { 	
            // - Set B panel key fields and sorting order
            DSP.W1LIVR.move(bookmark_LIVR); 	
            // Init sorting order 	
            DSP.WBQTTP = 1; 	
            setIndicatorForSortingOrder(DSP.WBQTTP);
            // Process option 	
            if (!processOptionInBookmark(bookmark, startPanel, startPanelSequence, DSP.WSOPT2)) { 	
               // Option already processed. Stop running application 	
               return false; 	
            }
         } else { 	
            // MSGID=X_00025 Invalid bookmark 	
            bookmark.setError("X_00025", ""); 	
            return false; 	
         } 	   	
      } else { 	
         // MSGID=X_00025 Invalid bookmark 	
         bookmark.setError("X_00025", ""); 	
         return false; 	
      } 	
      return true; 	
   } 	

   /**
   * Called to set key fields when processing bookmark in PBCHK.
   */
   public void setKeyFieldsInPBCHKFromBookmark() { 	
      SYSTP.setLIVR().move(bookmark_LIVR); 	
   } 	

   /**
   * Called in PBCHK to validate the selected record before processing it as a bookmark.
   */
   public void validateRecordInPBCHKInCaseOfBookmark() { 	
      // Set subfile hidden key fields from record. 	
      DSP.WSLIVR.move(SYSTP.getLIVR());
      // Nothing to check 	
   } 	

   /**
   * Save start values.
   */
   public void SAVSTR() {
      // Update start values
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      pSettings.indicateAutomated();
      pSettings.RESP.set().move(LDAZD.RESP);
      SAVSTR_setValues();
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      pSettings.prepare(cEnumStep.VALIDATE);
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      if (!pSettings.messages.existError()) {
         pSettings.prepare(cEnumStep.UPDATE);
         int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
         CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
      }
      pSettings.release();
   }
   
   /**
   * Set start values before saving.
   */
   public void SAVSTR_setValues() {
      if (!bookmarkInSession(DSPGM)) { 	
         pSettings.QTTP.set(DSP.WBQTTP);
      } 	
   }
   
   /**
   * Set last record - End of program
   */
   public void SETLR() {
      if (pMaintain != null) {
         pMaintain.messages.forgetNotifications();
      }
      if (pDelete != null) {
         pDelete.messages.forgetNotifications();
      }
      if (pCopy != null) {
         pCopy.messages.forgetNotifications();
      }
      if (pSettings != null) {
         pSettings.messages.forgetNotifications();
      }
      super.SETLR(true);
   }
   
   /**
   * Sets initial settings.
   * Note that some fields already get a default value by the function program.
   */
   public void setInitialSettings() {
      if (LDAZD.SPI2 == 'A') {
         // Override SPI2, since there is no A-panel
         pSettings.SPIC.set().move('B'); 
      } else {
         pSettings.SPIC.set().move(LDAZD.SPI2);
      }
   }

   /**
   * Validates the sorting order.
   * @param QTTP
   *    Sorting order
   * @return
   *    True if the specified sorting order is valid.
   */
   public boolean validSortingOrder(int QTTP) {
      return QTTP >= 1 && QTTP <=1;
   }

   /**
   * Sets the indicator corresponding to the given sorting order.
   * @param QTTP
   *    Sorting order
   */
   public void setIndicatorForSortingOrder(int QTTP) {
      moveToIN(61, "000000000");  // Clear IN61-69
      moveToIN(60 + QTTP, true);
   }

   /**
   * Initiate program.
   */
   public void INIT() {
      // Init keys
      SYSTP.setCONO(LDAZD.CONO);
      SYSTP.setDIVI().move(LDAZD.DIVI);
      SYSTP.setPGNM().move(this.DSPGM);
      this.PXCONO = LDAZD.CONO; 	
      this.PXDIVI.clear(); 	
      // Check authority
      this.PXPGNM.move(this.DSPGM);
      this.PXAUPF.moveRight(LDAZD.AUPF);
      PXAUTCHK.CAUTCHK();
      this.PXO.move(this.PXALOP);
      // - Not allowed to run program
      if (this.PXALPG == 0) {
         // MSGID=XAU0002 You are not authorized to run the program &1
         SRCOMRCM.MSGLVL.moveLeft("*PRV");
         COMPMQ("XAU0002", formatToString(this.DSPGM));
         SETLR();
         return;
      }
      // Check start values
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      pSettings.indicateAutomated();
      pSettings.RESP.set().move(LDAZD.RESP);
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      if (pSettings.messages.exists("XRE0103")) { // Record does not exist
         pSettings.prepare(cEnumStep.VALIDATE);
         setInitialSettings();
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS455Fnc", pSettings);
         LDAZZ.FPNM.move(XFPGNM);
         if (!pSettings.messages.existError()) {
            pSettings.prepare(cEnumStep.UPDATE);
            int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
            CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
         }
      }
      pSettings.release();
      // - Init panel sequence
      this.CSSQ.move(pSettings.DSEQ.get());
      seqLoad();
      // - Init start picture
      initStartPicture();
      // - Init sorting order
      DSP.WBQTTP = pSettings.QTTP.get();
      if (!validSortingOrder(DSP.WBQTTP)) {
         DSP.WBQTTP = 1;
      }
      setIndicatorForSortingOrder(DSP.WBQTTP);
      // Help with ACDT turned on
      setACDTtoCurrentDate = true;
      // Check for bookmark
      if (bookmarkInSession(DSPGM)) { 	
         // Process bookmark 	
         if (!processBookmark()) { 	
            // Abort after processing the bookmark 	
            SETLR(); 	
            return; 	
         } 	
      } 	
   }

   String getApName() {
      return "APS455AP";
   }

   // Movex MDB definitions
   public mvx.db.dta.CMNDIV MNDIV;
   public mvx.db.dta.CIDMAS IDMAS;
   public mvx.db.dta.FAPRCD APRCD;
   public mvx.db.dta.FAPIBH APIBH;
   // Movex MDB definitions end

   public void initMDB() {
      MNDIV = (mvx.db.dta.CMNDIV)getMDB("CMNDIV", MNDIV);
      IDMAS = (mvx.db.dta.CIDMAS)getMDB("CIDMAS", IDMAS);
      APRCD = (mvx.db.dta.FAPRCD)getMDB("FAPRCD", APRCD);
      APIBH = (mvx.db.dta.FAPIBH)getMDB("FAPIBH", APIBH);
  }

   public void initDSP() {
      if (DSP == null) {
         DSP = new APS455DSP(this);
      }
   }

   public cPXAPS455FncINmaintain pMaintain = null;
   public cPXAPS455FncINdelete pDelete = null;
   public cPXAPS455FncINcopy pCopy = null;
   public cPXAPS455FncINsettings pSettings = null;

   
   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS455FncINmaintain get_pMaintain() {
      if (pMaintain == null) {
         cPXAPS455FncINmaintain newPlist = new cPXAPS455FncINmaintain();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pMaintain.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pMaintain.allowUpdateWithErrors();
         return pMaintain;
      }
   }
   
   /**
   * Calling APS455Fnc with pMaintain as a transaction.
   */
   @Transaction(name=cPXAPS455FncINmaintain.LOGICAL_NAME)
   public void transaction_APS455FncINmaintain() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
   }
   
   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS455FncINdelete get_pDelete() {
      if (pDelete == null) {
         cPXAPS455FncINdelete newPlist = new cPXAPS455FncINdelete();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pDelete.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pDelete.allowUpdateWithErrors();
         return pDelete;
      }
   }
   
   /**
   * Calling APS455Fnc with pDelete as a transaction.
   */
   @Transaction(name=cPXAPS455FncINdelete.LOGICAL_NAME)
   public void transaction_APS455FncINdelete() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pDelete);
      LDAZZ.FPNM.move(XFPGNM);
   }
   
   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS455FncINcopy get_pCopy() {
      if (pCopy == null) {
         cPXAPS455FncINcopy newPlist = new cPXAPS455FncINcopy();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pCopy.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pCopy.allowUpdateWithErrors();
         return pCopy;
      }
   }
   
   /**
   * Calling APS455Fnc with pCopy as a transaction.
   */
   @Transaction(name=cPXAPS455FncINcopy.LOGICAL_NAME)
   public void transaction_APS455FncINcopy() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pCopy);
      LDAZZ.FPNM.move(XFPGNM);
   }
   
   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS455FncINsettings get_pSettings() {
      if (pSettings == null) {
         cPXAPS455FncINsettings newPlist = new cPXAPS455FncINsettings();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pSettings.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pSettings.allowUpdateWithErrors();
         return pSettings;
      }
   }
   
   /**
   * Calling APS455Fnc with pSettings as a transaction.
   */
   @Transaction(name=cPXAPS455FncINsettings.LOGICAL_NAME)
   public void transaction_APS455FncINsettings() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
   }

   public int bookmark_CONO; 	
   public MvxString bookmark_DIVI = cRefDIVI.likeDef();	
   public MvxString bookmark_PGNM = cRefPGNM.likeDef();
   public MvxString bookmark_RESP = cRefRESP.likeDef();
   public MvxString bookmark_LIVR = cRefLIVR.likeDef();
   public char startPanel; 	
   public MvxString startPanelSequence = cRefPSEQ.likeDef();
   public int startSortingOrder;
   public boolean option9FromBookmark;   
   public MvxString XJLIVR = cRefLIVR.likeDef();
   public MvxString XLLIVR = cRefLIVR.likeDef();
   public MvxString SSLIVR = cRefLIVR.likeDef();
   public MvxString XFPGNM = cRefPGNM.likeDef();
   public MvxString presetDIVI = cRefDIVI.likeDef();
   public MvxString selectedDIVI = cRefDIVI.likeDef();
   public long presetINBN;
   public int presetIBOP;
   public boolean updateSelection;
   public boolean submitSelection;
   public boolean found_CSYSTP;
   public boolean passCSYSTP;
   public char lastPanel;
   public boolean preparePanelC;
   public boolean preparePanelD;
   public boolean preparePanelE;
   public boolean preparePanelP;
   public boolean errorOnInitiate;
   public cCRCommon CRCommon = new cCRCommon(this);
   public cCRCalendar CRCalendar = new cCRCalendar(this);
   public cCROutput CROutput = new cCROutput(this);
   public sCRMessageDS CRMessageDS = new sCRMessageDS(this);
   public cPXMNS210 PXMNS210 = new cPXMNS210(this);
   public cPXMNS230 PXMNS230 = new cPXMNS230(this);
   public sMNS213DS MNS213DS = new sMNS213DS(this); 	
   public MvxRecord pMNS213 = new MvxRecord(); 	
   public boolean setACDTtoCurrentDate;

   public MvxStruct rAPIDS = new MvxStruct(413);
   public MvxString APIDS = rAPIDS.newString(0, 413);
   public MvxString PXENV = rAPIDS.newChar(0);
   public MvxString PXOPC = rAPIDS.newString(1, 10);
   public MvxString PXIN60 = rAPIDS.newChar(11);
   public MvxString PXMSID = rAPIDS.newString(12, 7);
   public MvxString PXMSGD = rAPIDS.newString(19, 256);
   public MvxString PXMSG = rAPIDS.newString(275, 128);
   public MvxString PXCHID = rAPIDS.newString(403, 10);

   public APS455DSP DSP;

   public GenericDSP getDSP() {
      return (GenericDSP)DSP;
   }

   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(DSP);
      v.addElement(MNDIV);
      v.addElement(IDMAS);
      v.addElement(APRCD);
      v.addElement(APIBH);
      v.addElement(pMaintain);
      v.addElement(pDelete);
      v.addElement(pCopy);
      v.addElement(pSettings);
      v.addElement(bookmark_DIVI);
      v.addElement(bookmark_PGNM);
      v.addElement(bookmark_RESP);
      v.addElement(bookmark_LIVR);
      v.addElement(startPanelSequence);
      v.addElement(XJLIVR);
      v.addElement(XLLIVR);
      v.addElement(SSLIVR);
      v.addElement(XFPGNM);
      v.addElement(presetDIVI);
      v.addElement(selectedDIVI);
      v.addElement(CRCommon);
      v.addElement(CRCalendar);
      v.addElement(CROutput);
      v.addElement(CRMessageDS);
      v.addElement(PXMNS210);
      v.addElement(PXMNS230);
      v.addElement(MNS213DS);
      v.addElement(rAPIDS);
      return version;
   }

   public void clearInstance() {
      super.clearInstance();
      bookmark_CONO = 0;
      startPanel = ' ';
      startSortingOrder = 0;
      option9FromBookmark = false;
      presetINBN = 0L;
      presetIBOP = 0;
      updateSelection = false;
      submitSelection = false;
      found_CSYSTP = false;
      passCSYSTP = false;
      lastPanel = ' ';
      preparePanelC = false;
      preparePanelD = false;
      preparePanelE = false;
      preparePanelP = false;
      errorOnInitiate = false;
      setACDTtoCurrentDate = false;
   }
   
   public String getBookmarkTableName() { 	
     return "CSYSTP"; 	
   } 	

   public static String[][] getBookmarkParameters() { 
     return bookmarkParameters; 
   }       

   public final static String[][] bookmarkParameters = {
      // PARAM       - The parameter identifier corresponding to a field in the program or view def (max 10 characters)
      // TYPE        - The parameter type: ALPHA, NUMERIC, or DATE
      // NAME        - The name of the parameter (max 15 characters)
      // DESCRIPTION - The purpose of the parameter (max 200 characters)
      //      
      //PARAM        TYPE       NAME               DESCRIPTION
      {"presetDIVI", "ALPHA",   "Division",        "Preset selected division (only used for personal report version)."},
      {"presetINBN", "NUMERIC", "Invoice",         "Preset invoice INBN (only used for personal report version)."},
      {"presetIBOP", "NUMERIC", "Operation",       "Preset operation IBOP (only used for personal report version)."},
   };

   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getPanelsRequiredForAdd() {
      return APS455Fnc.getPanelsRequiredForAdd();
   }

   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getAllowedPanels() {
      return APS455Fnc.getAllowedPanels();
   }

   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getDefaultPanels() {
      return APS455Fnc.getDefaultPanels();
   }
   
public final static String _version="15";

public final static String _release="1";

public final static String _spLevel="2";

public final static String _spNumber="";

public final static String _GUID="417267C780284731B83C2996A883DD4F";

public final static String _tempFixComment="";

public final static String _build="000000000000084";

public final static String _pgmName="APS455";

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
